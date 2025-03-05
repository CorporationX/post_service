package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCorrectionDto;
import faang.school.postservice.exception.PostAlreadyPublishedException;
import faang.school.postservice.exception.PostWasDeletedException;
import faang.school.postservice.exception.ProjectNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Service
public class PostService {

    @Value("${post-service.publish.batch-size}")
    private int batchSize;

    @Value("${post-processing.max-batches-amount}")
    private int maxBatchesAmount;

    @Value("${post-processing.posts-batch-size}")
    private int postsBatchSize;

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final AsyncPostPublishPerformer publishPerformer;
    private final WebClient webClient;

    @Transactional
    public void createPostByUserId(Long userId, Post post) {
        doesUserExist(userId);
        post.setAuthorId(userId);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    @Transactional
    public void createPostByProjectId(Long projectId, Post post) {
        doesProjectExist(projectId);
        post.setProjectId(projectId);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    @Transactional
    public void publishPost(Long postId) {
        Post post = getPost(postId);

        if (post.getPublishedAt() != null) {
            throw new PostAlreadyPublishedException("The post has already been published");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Transactional
    public void updatePost(Long postId, Post post) {
        Post existingPost = getPost(postId);

        existingPost.setContent(post.getContent());
        existingPost.setUpdatedAt(LocalDateTime.now());
        existingPost.setProjectId(post.getProjectId());

        postRepository.save(existingPost);
    }

    @Transactional
    public void softDeletePost(Long postId) {
        Post existingPost = getPost(postId);

        existingPost.setDeleted(true);

        postRepository.save(existingPost);
    }

    public Post getPostById(Long postId) {
        Post post = getPost(postId);

        if (post.isDeleted()) {
            throw new PostWasDeletedException("The post was deleted");
        }

        return post;
    }

    public List<Post> getNotPublishedPostsByUser(Long userId) {
        doesUserExist(userId);
        return postRepository
                .findByAuthorId(userId).stream()
                .filter(post -> !post.isPublished())
                .collect(Collectors.toList());
    }

    public List<Post> getNotPublishedPostsByProject(Long projectId) {
        doesProjectExist(projectId);
        return postRepository
                .findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished())
                .collect(Collectors.toList());

    }

    public List<Post> getPublishedPostsByUser(Long userId) {
        doesUserExist(userId);
        return postRepository
                .findByAuthorIdWithLikes(userId).stream()
                .filter(Post::isPublished)
                .collect(Collectors.toList());
    }

    public List<Post> getPublishedPostsByProject(Long projectId) {
        doesProjectExist(projectId);
        return postRepository
                .findByProjectIdWithLikes(projectId).stream()
                .filter(Post::isPublished)
                .collect(Collectors.toList());
    }

    @Transactional
    public void savePost(Post post) {
        postRepository.save(post);
    }

    @Transactional
    public void publishScheduledPosts() {
        List<Post> readyToPublishPosts = postRepository.findReadyToPublish();

        if (readyToPublishPosts.isEmpty()) {
            return;
        }

        List<List<Post>> batches = ListUtils.partition(readyToPublishPosts, batchSize);

        batches.forEach(publishPerformer::publishBatch);
    }

    @Transactional
    public void correctUnpublishedPosts() {
        List<Post> posts = postRepository.findReadyToPublish();

        Flux.fromIterable(posts)
                .buffer(postsBatchSize)
                .take(maxBatchesAmount)
                .flatMap(batch -> Flux.fromIterable(batch)
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(this::processPost, 1)
                        .onErrorContinue((throwable, o) ->
                                log.error("Error processing batch: {}", throwable.getMessage()))

                )
                .blockLast(Duration.ofSeconds(60));

    }

    @Transactional
    @Retryable(
            value = Exception.class,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void correctPostContent(Post post) {
        String originalContent = post.getContent();
        try {
            List<PostCorrectionDto> results = checkTextWithYandexSpeller(originalContent).block();

            if (results != null && !results.isEmpty()) {
                String correctedContent = applyCorrections(originalContent, results);
                if (!correctedContent.equals(originalContent)) {
                    post.setContent(correctedContent);
                    try {
                        postRepository.save(post);
                    } catch (OptimisticLockingFailureException e) {
                        log.warn("Optimistic locking failure while saving post id {}. Retrying", post.getId());
                    }
                }
            } else {
                log.info("No errors found in post id {}", post.getId());
            }
        } catch (Exception e) {
            log.error("Error during spell check for post id {}: {}", post.getId(), e.getMessage(), e);
            throw e;
        }
    }

    private Mono<Void> processPost(Post post) {
        return Mono.fromRunnable(() -> {
                    try {
                        correctPostContent(post);
                    } catch (Exception e) {
                        log.error("Error correcting post id {}: {}", post.getId(), e.getMessage(), e);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private Mono<List<PostCorrectionDto>> checkTextWithYandexSpeller(String text) {
        log.info("Sending text to speller for post id: {}", text.substring(0, Math.min(text.length(), 50)));

        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);

            return webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("text", encodedText)
                            .build())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToFlux(PostCorrectionDto.class)
                    .collectList()
                    .onErrorResume(e -> {
                        log.error("Error during speller API call: {}", e.getMessage(), e);
                        return Mono.empty();
                    });
        } catch (Exception e) {
            log.error("Error encoding text: {}", e.getMessage(), e);
            return Mono.error(e);
        }
    }

    private String applyCorrections(String text, List<PostCorrectionDto> results) {
        StringBuilder correctedText = new StringBuilder(text);
        int offset = 0;

        for (PostCorrectionDto result : results) {
            if (result.getHints() != null && !result.getHints().isEmpty()) {
                String suggestedWord = result.getHints().get(0);
                int start = result.getErroneousWordStartIndex() + offset;
                int end = start + result.getErroneousWordLength();

                if (start >= 0 && end <= correctedText.length()) {
                    correctedText.replace(start, end, suggestedWord);
                    offset += suggestedWord.length() - result.getErroneousWordLength();
                } else {
                    log.warn("Invalid position for correction, skip. {}, Current text length: {}", result, correctedText.length());
                }
            }
        }
        return correctedText.toString();
    }

    private void doesUserExist(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
    }

    private void doesProjectExist(Long projectId) {
        try {
            projectServiceClient.getProject(projectId);
        } catch (FeignException.NotFound e) {
            throw new ProjectNotFoundException("Project with id " + projectId + " not found");
        }
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }
}

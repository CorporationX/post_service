package faang.school.postservice.service;

import faang.school.postservice.client.PostCorrecterClient;
import com.google.common.collect.Lists;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.postCorrecter.PostCorrecterRequest;
import faang.school.postservice.dto.postCorrecter.PostCorrecterResponse;
import faang.school.postservice.dto.postCorrecter.textGears.TextGearsRequest;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.utils.KafkaSender;
import faang.school.postservice.utils.PublishedPostMessage;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostMapper postMapper;
    private final PostCorrecterClient postCorrecterClient;
    @Value("${side-api.text-gears.key}")
    private String apiKey;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final KafkaSender kafkaSender;

    public PostDto createPostDraft(PostDto postDto) {
        validateAuthor(postDto);
        checkEntityExistence(postDto.getAuthorId(), "User", userServiceClient::getUser);
        checkEntityExistence(postDto.getProjectId(), "Project", projectServiceClient::getProject);

        Post postToSave = postMapper.toEntity(postDto);
        postToSave.setPublished(false);
        postToSave.setDeleted(false);

        Post savedPost = postRepository.save(postToSave);
        log.info("Post was created with ID: {}", savedPost.getId());
        return postMapper.toDto(savedPost);
    }

    public PostDto publishPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        validatePostForPublishing(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Post publishedPost = postRepository.save(post);
        UserDto user = userServiceClient.getUser(publishedPost.getAuthorId());

        kafkaSender.send(new PublishedPostMessage(publishedPost, user), "posts");

        PostDto postDto = postMapper.toDto(publishedPost);
        postDto.setLikeCount(countLikesForPost(postId));
        log.info("Post with ID: {} was published", postId);
        return postDto;
    }

    public PostDto updatePost(Long postId, PostDto postDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        if (post.isDeleted()) {
            throw new PostValidationException("Post is already deleted with ID: " + postId);
        }

        if (postDto.getContent() != null) {
            if (postDto.getContent().isBlank()) {
                throw new PostValidationException("Post content cannot be empty, post ID: " + postId);
            }
            post.setContent(postDto.getContent());
            post.setUpdatedAt(LocalDateTime.now());
        }

        Post updatedPost = postRepository.save(post);

        PostDto postDtoToReturn = postMapper.toDto(updatedPost);
        postDtoToReturn.setLikeCount(countLikesForPost(postId));
        log.info("Post with ID: {} was updated", postId);
        return postDtoToReturn;
    }

    public void softDelete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        if (post.isDeleted()) {
            throw new PostValidationException("Post is already deleted with ID: " + postId);
        }

        post.setDeleted(true);
        postRepository.save(post);
        log.info("Post with ID: {} was deleted softly", postId);
    }

    public PostDto getPostById(Long postId) {
        Post post = postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with ID: " + postId));

        PostDto postDto = postMapper.toDto(post);
        postDto.setLikeCount(countLikesForPost(postId));
        log.info("Fetch the post with ID: {}", postId);
        return postDto;
    }

    public List<PostDto> getAllPostDraftsByUserId(Long userId) {
        List<Post> postDrafts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
        log.info("Fetch all post drafts of the user with ID: {}", userId);
        return postMapper.toDto(postDrafts);
    }

    public List<PostDto> getAllPostDraftsByProjectId(Long projectId) {
        List<Post> postDrafts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
        log.info("Fetch all post drafts of the project with ID: {}", projectId);
        return postMapper.toDto(postDrafts);
    }

    public List<PostDto> getAllPublishedPostsByUserId(Long userId) {
        List<PostDto> posts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .map(postDto -> {
                    postDto.setLikeCount(countLikesForPost(postDto.getId()));
                    return postDto;
                })
                .toList();
        log.info("Fetch all posts of the user with ID: {}", userId);
        return posts;
    }

    public List<PostDto> getAllPublishedPostsByProjectId(Long projectId) {
        List<PostDto> posts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .peek(postDto -> postDto.setLikeCount(countLikesForPost(postDto.getId())))
                .toList();
        log.info("Fetch all posts of the project with ID: {}", projectId);
        return posts;
    }

    public void publishScheduledPosts(){
        List<Post> postsToPublish = postRepository.findReadyToPublish();

        if (postsToPublish.isEmpty()) {
            return;
        }

        int batchSize = 1000;
        List<List<Post>> batches = Lists.partition(postsToPublish, batchSize);

        for (List<Post> batch : batches) {
            threadPoolExecutor.submit(() -> publishBatch(batch));
        }
    }

    private void publishBatch(List<Post> batch) {
        batch.forEach(post -> {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        });
        postRepository.saveAll(batch);
    }

    private void validateAuthor(PostDto postDto) {
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new PostValidationException("A post can be created by a user or a project");
        }

        if (postDto.getAuthorId() == null && postDto.getProjectId() == null) {
            throw new PostValidationException("A post must be created by a user or a project");
        }
    }

    private <T> void checkEntityExistence(Long id, String entityType, Function<Long, T> clientCall) {
        if (id != null) {
            try {
                T entity = clientCall.apply(id);
                if (entity == null) {
                    throw new EntityNotFoundException(entityType + " not found with ID: " + id);
                }
            } catch (FeignException.NotFound e) {
                log.warn("{} not found with ID: {}", entityType, id, e);
                throw new EntityNotFoundException(entityType + " Service returned 404 - " + entityType + " not found with ID: " + id);
            } catch (FeignException e) {
                log.error("Error while communicating with {} Service: {}", entityType, e.getMessage(), e);
                throw new ExternalServiceException("Failed to communicate with " + entityType + " Service. Please try again later.");
            }
        }
    }

    private void validatePostForPublishing(Post post) {
        if (post.isPublished()) {
            throw new PostValidationException("The post is already published");
        }
        if (post.isDeleted()) {
            throw new PostValidationException("Post is already deleted with ID: " + post.getId());
        }
    }

    private int countLikesForPost(Long postId) {
        return postRepository.countLikesByPostId(postId);
    }

    @Transactional
    public void sendPostToSpellingCheck() {
        List<Post> readyToPublishPosts = postRepository.findReadyToPublish();
        readyToPublishPosts.forEach(post -> {
            PostCorrecterRequest request = TextGearsRequest.builder()
                    .text(post.getContent())
                    .key(apiKey)
                    .build();

            try {
                correctAndSavePost(post, request);
            } catch (ExternalServiceException e) {
                log.error("Error while communicating with external service", e);
            }
        });
    }

    @Retryable(
            retryFor = {FeignException.class},
            backoff = @Backoff(delay = 500, multiplier = 2)
    )
    @Async("poolForCron")
    public void correctAndSavePost(Post post, PostCorrecterRequest request) {
        PostCorrecterResponse response = postCorrecterClient.checkPost(request);
        if (response.isSuccess()) {
            post.setContent(response.getCorrectedPost());
            postRepository.save(post);
        }
        throw new ExternalServiceException("Failed to communicate with external service. Please try again later.");
    }
}
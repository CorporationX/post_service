package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.kafka.events.PostFollowersEvent;
import faang.school.postservice.kafka.producer.KafkaEventProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.feed.AuthorCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final InternalServices internalServices;
    private final AsyncModerationService asyncModerationService;
    private final SpellCheckerService spellCheckerService;
    private final PostMapper postMapper;
    private final AuthorCacheService authorCacheService;
    private final ExecutorService executorService;
    private final KafkaEventProducer kafkaEventProducer;
    private final UserServiceClient userServiceClient;

    @Value("${moderation.threadSize}")
    private int threadSize;

    public PostDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Specified post not found. Id:" + postId));
        return postMapper.toDto(post);
    }

    @Transactional
    public Post createDraft(Post post) {
        if (post.getAuthorId() != null && !internalServices.userExists(post.getAuthorId())) {
            throw new InvalidParameterException("Post author does not exist! id:" + post.getAuthorId());
        }
        if (post.getProjectId() != null && !internalServices.projectExists(post.getProjectId())) {
            throw new InvalidParameterException("Post project does not exist! id:" + post.getProjectId());
        }
        return postRepository.save(post);
    }

    @Transactional
    public Post publish(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Specified post not found. Id:" + postId));

        if (post.isPublished()) {
            throw new DataValidationException("Post is already published. Id:" + postId);
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        authorCacheService.saveAuthorCache(post.getAuthorId());

        UserDto author = userServiceClient.getUser(post.getAuthorId());
        List<Long> followers = author.getFollowers();

        kafkaEventProducer.sendPostFollowersEvent(PostFollowersEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .followersIds(followers)
                .publishedAt(post.getPublishedAt())
                .build());

        return postRepository.save(post);
    }

    @Transactional
    public Post update(Post post) {
        Post originalPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new DataValidationException("You are trying to update not existing post. Id:" + post.getId()));
        if (!Objects.equals(originalPost.getAuthorId(), post.getAuthorId())
                || !Objects.equals(originalPost.getProjectId(), post.getProjectId())) {
            throw new DataValidationException("Post author cannot be changed!");
        }
        return postRepository.save(post);
    }

    @Transactional
    public void delete(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Specified post not found. Id:" + postId));
        post.setDeleted(true);
        postRepository.save(post);
    }

    public Post get(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Specified post not found. Id:" + postId));
    }

    public List<Post> getDraftsByAuthorId(Long userId) {
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    public List<Post> getDraftsByProjectId(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    public List<Post> getPostsByAuthorId(Long userId) {
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }

    public List<Post> getPostsByProjectId(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public void moderatePosts() {
        List<Post> posts = postRepository.findByVerifiedDateIsNull();
        if (posts == null || posts.isEmpty()) return;

        List<List<Post>> threads = splitIntoThreads(posts);
        List<CompletableFuture<Void>> futures = threads.stream()
                .map(asyncModerationService::moderateThreadAsync)
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    public List<PostDto> getPostsByIds(List<Long> ids) {
        List<Post> posts = StreamSupport
                .stream(postRepository.findAllById(ids).spliterator(), false)
                .toList();
        return postMapper.toDto(posts);
    }

    public List<Post> findPostsByResourceKeys(List<String> resourceKeys) {
        return postRepository.findPostsByResourceKeys(resourceKeys);
    }

    public List<Long> getUsersForBanWithUnverifiedPosts(int maxUnverifiedPosts) {
        return postRepository.findUserIdsToBanWithUnverifiedPosts(maxUnverifiedPosts);
    }

    @Transactional
    public void publishScheduledPosts() {
        List<Post> postsToPublish = postRepository.findReadyToPublish();
        List<List<Post>> batches = ListUtils.partition(postsToPublish, 1000);

        try {
            List<Callable<Void>> tasks = batches.stream()
                    .map(this::publishChunkOfPosts)
                    .toList();
            executorService.invokeAll(tasks);
        } catch (Exception e) {
            log.error("Publishing posts chunk failed!", e);
        }
        CompletableFuture.completedFuture(null);
    }

    @Transactional
    public void correctPosts() {
        int page = 0;
        int batchSize = spellCheckerService.calculateBatchSize();
        Pageable pageable = PageRequest.of(page, batchSize);

        do {
            Page<Post> postsPage = postRepository.findByPublishedFalse(pageable);
            if (postsPage == null || postsPage.isEmpty()) break;

            List<Post> posts = postsPage.getContent();
            if (posts.isEmpty()) break;

            List<String> contents = posts.stream().map(Post::getContent).toList();
            try {
                List<String> corrected = spellCheckerService.sendBatchRequestToYandexSpeller(contents);
                if (corrected.size() != contents.size()) continue;

                for (int i = 0; i < posts.size(); i++) {
                    posts.get(i).setContent(corrected.get(i));
                }

                postRepository.saveAll(posts);
            } catch (Exception ex) {
                log.error("Failed to process batch: {}", ex.getMessage());
            }

            pageable = postsPage.nextPageable();
        } while (pageable.isPaged());
    }

    private List<List<Post>> splitIntoThreads(List<Post> posts) {
        return ListUtils.partition(posts, threadSize);
    }

    private Callable<Void> publishChunkOfPosts(List<Post> postsToPublish) {
        return () -> {
            postsToPublish.forEach(post -> {
                post.setPublished(true);
                post.setPublishedAt(LocalDateTime.now());
            });
            postRepository.saveAll(postsToPublish);
            return null;
        };
    }
}
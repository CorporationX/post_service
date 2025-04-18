package faang.school.postservice.service;

import faang.school.postservice.annotations.PublishPostEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.post.view.AnalyticsPostViewEvent;
import faang.school.postservice.model.event.post.view.NotificationPostViewEvent;
import faang.school.postservice.repository.PostRepository;
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
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final InternalServices internalServices;
    private final ExecutorService executorService;
    private final AsyncModerationService asyncModerationService;
    private final SpellCheckerService spellCheckerService;
    private final KafkaPostProducer kafkaPostProducer;
    private final PostCacheService postCacheService;
    private final UserCashService userCashService;

    @Value("${moderation.threadSize}")
    private int threadSize;

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
        Post result = postRepository.save(post);
        kafkaPostProducer.publishPostCreationEvent(result);
        postCacheService.cachePost(result);

        userCashService.cacheUser(result.getAuthorId());

        return result;
    }

    @Transactional
    public Post update(Post post) {
        Post originalPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new DataValidationException("You are trying to update not existing post. Id:"
                        + post.getId()));
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
        postCacheService.removePostFromCache(postId);
    }

    @PublishPostEvent(events = {AnalyticsPostViewEvent.class, NotificationPostViewEvent.class})
    public Post get(Long postId) {
        Optional<Post> cachedPost = postCacheService.getCachedPost(postId);

        return cachedPost.orElseGet(() -> postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Specified post not found. Id:" + postId)));

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

    @PublishPostEvent(events = {AnalyticsPostViewEvent.class})
    public List<Post> getPostsByAuthorId(Long userId) {
        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }

    @PublishPostEvent(events = {AnalyticsPostViewEvent.class})
    public List<Post> getPostsByProjectId(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public void moderatePosts() {
        List<Post> posts = postRepository.findByVerifiedDateIsNull();

        if (posts == null || posts.isEmpty()) {
            return;
        }

        List<List<Post>> threads = splitIntoThreads(posts);

        List<CompletableFuture<Void>> futures = threads.stream()
                .map(asyncModerationService::moderateThreadAsync)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private List<List<Post>> splitIntoThreads(List<Post> posts) {
        return ListUtils.partition(posts, threadSize);
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

        List<List<Post>> postsToPublishPartitioned = ListUtils.partition(postsToPublish, 1000);
        try {
            List<Callable<Void>> tasks = postsToPublishPartitioned.stream()
                    .map(this::publishChunkOfPosts)
                    .toList();

            executorService.invokeAll(tasks);
        } catch (Exception e) {
            log.error("Publishing posts chunk failed!", e);
        }
        CompletableFuture.completedFuture(null);
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

    @Transactional
    public void correctPosts() {
        int page = 0;
        int batchSize = spellCheckerService.calculateBatchSize();
        Pageable pageable = PageRequest.of(page, batchSize);

        do {
            Page<Post> postsPage = postRepository.findByPublishedFalse(pageable);

            if (postsPage == null || postsPage.isEmpty()) {
                log.error("Received null page from repository");
                break;
            }

            List<Post> posts = postsPage.getContent();

            if (posts.isEmpty()) {
                log.info("No more posts to process");
                break;
            }

            List<String> contents = posts.stream()
                    .map(Post::getContent)
                    .collect(Collectors.toList());

            try {
                List<String> correctedContents = spellCheckerService.sendBatchRequestToYandexSpeller(contents);

                if (correctedContents.size() != contents.size()) {
                    log.error("Size mismatch: correctedContents size = {}, contents size = {}",
                            correctedContents.size(), contents.size());
                    continue;
                }

                for (int i = 0; i < posts.size(); i++) {
                    Post post = posts.get(i);
                    String correctedContent = correctedContents.get(i);
                    post.setContent(correctedContent);
                }

                postRepository.saveAll(posts);
            } catch (Exception ex) {
                log.error("Failed to process batch: {}", ex.getMessage());
            }

            pageable = postsPage.nextPageable();
        } while (pageable.isPaged());
    }
}

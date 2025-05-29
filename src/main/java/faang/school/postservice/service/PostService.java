package faang.school.postservice.service;

import com.google.common.collect.Lists;
import faang.school.postservice.client.SubscriptionServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.ModerationProperties;
import faang.school.postservice.dto.post.CreatePostRequest;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.event.PostCreatedEvent;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.PostPublishingException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.moderation.AsyncModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private static final String POST = "Post";

    private final GrammarBotService grammarBotService;
    private final PostRepository postRepository;
    private final AsyncModerationService asyncModerationService;
    private final ModerationProperties moderationProperties;
    private final UserServiceClient userServiceClient;
    private final SubscriptionServiceClient subscriptionServiceClient;
    private final PostMapper postMapper;
    private final KafkaPostProducer kafkaPostProducer;

    private static final int BATCH_SIZE = 1000;

    @Transactional(propagation = Propagation.REQUIRED)
    public void moderateAllUnverifiedPosts() {
        Integer batchSize = moderationProperties.getBatchSize();
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be greater than 0");
        }
        List<Post> unverifiedPosts = postRepository.findByVerifiedAtIsNull();
        if (unverifiedPosts.isEmpty()) {
            log.warn("No unverified posts found for moderation");
            return;
        }

        List<List<Post>> batches = ListUtils.partition(unverifiedPosts, batchSize);

        List<CompletableFuture<Void>> futures = batches.stream()
                .map(asyncModerationService::moderateBatchAsync)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .exceptionally(ex -> {
                    log.error("Moderation batch failed", ex);
                    return null;
                });
    }

    @Transactional
    public PostResponseDto createPost(CreatePostRequest request) {
        try {
            userServiceClient.getUser(request.getAuthorId());
        } catch (Exception e) {
            log.warn("Author with ID {} not found in user service: {}", request.getAuthorId(), e.getMessage());
            throw new EntityNotFoundException("Author", request.getAuthorId());
        }

        Post post = postMapper.toEntity(request);
        post = postRepository.save(post);

        List<Long> subscriberIds = subscriptionServiceClient.getFolloweeIds(post.getAuthorId());

        PostCreatedEvent event = PostCreatedEvent.builder()
                .postId(post.getId())
                .text(post.getContent())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .timestamp(System.currentTimeMillis())
                .subscriberIds(subscriberIds)
                .build();

        kafkaPostProducer.sendPostCreatedEvent(event);

        return postMapper.toResponseDto(post);
    }

    public Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST, postId));

        log.info("Get post with id {}", postId);
        return post;
    }

    @Retryable(retryFor = Exception.class, backoff = @Backoff(delay = 2000, multiplier = 2))
    public void correctUnpublishedPosts() {
        List<Post> posts = postRepository.findReadyToPublish();

        for (Post post : posts) {
            try {
                String correctedText = grammarBotService.checkGrammar(post.getContent());
                post.setContent(correctedText);
                postRepository.save(post);
            } catch (Exception e) {
                log.error("Error checking post {}: {}", post.getId(), e.getMessage());
            }
        }
    }

    @Async("fileUploadTaskExecutor")
    @Transactional
    public CompletableFuture<Void> publishSchedulePosts() {
        List<Post> postsToPublish = postRepository.findReadyToPublish();

        if (postsToPublish.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        List<List<Post>> batches = Lists.partition(postsToPublish, BATCH_SIZE);

        List<CompletableFuture<Void>> futures = batches.parallelStream()
                .map(batch -> CompletableFuture.runAsync(() -> publishBatch(batch)))
                .toList();

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allFutures.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("Error while publishing posts: {}", throwable.getMessage(), throwable);
            } else {
                log.info("All posts have been successfully published (total: {} )", postsToPublish.size());
            }
        });
    }

    private void publishBatch(List<Post> batch) {
        try {
            batch.forEach(post -> {
                post.setPublished(true);
                post.setPublishedAt(LocalDateTime.now());
            });
            postRepository.saveAll(batch);
        } catch (Exception e) {
            log.error("Failed to publish batch of size {}", batch.size(), e);
            throw new PostPublishingException(
                    String.format("Failed to publish batch of size %d. Reason: %s",
                            batch.size(), e.getMessage()), e);
        }
    }
}

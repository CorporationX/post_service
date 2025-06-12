package faang.school.postservice.service;

import faang.school.postservice.config.ModerationProperties;
import com.google.common.collect.Lists;
import faang.school.postservice.config.transactional.PostTransactionService;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.PostDtoValidationException;
import faang.school.postservice.exception.PostPublishingException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.batch.PostEventBatchSender;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private static final String POST = "Post";

    private final GrammarBotService grammarBotService;
    private final PostRepository postRepository;
    private final AsyncModerationService asyncModerationService;
    private final ModerationProperties moderationProperties;
    private final PostMapper postMapper;
    private final PostEventBatchSender postEventBatchSender;
    private final PostTransactionService postTransactionService;

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

    public Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST, postId));

        log.info("Get post with id {}", postId);
        return post;
    }

    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
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
    public  CompletableFuture<Void> publishSchedulePosts() {
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

    @Async("fileUploadTaskExecutor")
    public PostDto publishPost(PostDto postDto) {
        Post post = validateDataForPublication(postDto.getId());

        Post savedPost = postTransactionService.saveAndPublishPost(post);
        postEventBatchSender.sendBatch(savedPost);
        return postMapper.toDto(savedPost);
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

    private Post validateDataForPublication(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new PostDtoValidationException(String.format("Post with ID %d does not exist", postId))
        );

        if (post.isDeleted()) {
            throw new PostDtoValidationException(String.format(
                    "The post with ID %d removed", postId));
        }

        if (post.isPublished()) {
            throw new PostDtoValidationException(String.format(
                    "The post with ID %d has already been published", postId));
        }

        return post;
    }
}

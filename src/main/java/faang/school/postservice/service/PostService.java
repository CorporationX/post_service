package faang.school.postservice.service;

import faang.school.postservice.config.ModerationProperties;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.moderation.AsyncModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private static final String POST = "Post";

    private final GrammarBotService grammarBotService;
    private final PostRepository postRepository;
    private final AsyncModerationService asyncModerationService;
    private final ModerationProperties moderationProperties;


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
}

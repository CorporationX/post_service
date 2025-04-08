package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.moderation.BatchProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private static final String POST = "Post";

    private final PostRepository postRepository;
    private final BatchProcessorService batchProcessorService;

    @Value("${moderation.batch-size}")
    private int batchSize;

    @Transactional
    public void moderateAllUnverifiedPosts() {
        List<Post> unverifiedPosts = postRepository.findByVerifiedAtIsNull();
        List<List<Post>> batches = partitionList(unverifiedPosts, batchSize);

        List<CompletableFuture<Void>> futures = batches.stream()
                .map(this::moderateBatchAsync)
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .exceptionally(ex -> {
                    log.error("Moderation failed for some batches", ex);
                    return null;
                })
                .join();
    }

    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            partitions.add(list.subList(i, end));
        }
        return partitions;
    }

    public Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST, postId));

        log.info("Get post with id {}", postId);
        return post;
    }

    @Async("fileUploadTaskExecutor")
    public CompletableFuture<Void> moderateBatchAsync(List<Post> batch) {
        try {
            batchProcessorService.processBatch(batch);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Batch moderation failed", e);
            return CompletableFuture.failedFuture(e);
        }
    }
}

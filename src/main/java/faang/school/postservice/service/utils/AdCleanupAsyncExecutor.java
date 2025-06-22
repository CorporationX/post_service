package faang.school.postservice.service.utils;

import faang.school.postservice.repository.adapter.AdRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdCleanupAsyncExecutor {
    private final AdRepositoryAdapter adRepositoryAdapter;

    @Async("adTaskExecutor")
    public CompletableFuture<Integer> deleteExpiredAdAsync(List<Long> batchToRemove, int currentBatchNum) {
        if (batchToRemove.isEmpty()) {
            log.info("No ads to delete in batch number {}, returning 0.",
                    currentBatchNum);
            return CompletableFuture.completedFuture(0);
        }
        log.info("Starting deleteAdsBatchAsync for batch number {}", currentBatchNum);
        try {
            if (Thread.currentThread().isInterrupted()) {
                log.warn("Thread {} was interrupted, stopping deleteAdsBatchAsync.",
                        Thread.currentThread().getName());
                return CompletableFuture.completedFuture(0);
            }
            adRepositoryAdapter.deletePostAds(batchToRemove);
            log.info("Successfully deleted batch number {}", currentBatchNum);
            return CompletableFuture.completedFuture(batchToRemove.size());
        } catch (Exception e) {
            log.error("Error deleting batch number {}: {}", currentBatchNum, e.getMessage(), e);
            CompletableFuture<Integer> failureFuture = new CompletableFuture<>();
            failureFuture.completeExceptionally(new RuntimeException("Error deleting ads in batch " + currentBatchNum));
            return failureFuture;
        }
    }
}

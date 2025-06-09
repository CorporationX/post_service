package faang.school.postservice.repository.adapter;

import faang.school.postservice.model.ad.AdStatus;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdRepositoryAdapter {
    private final AdRepository adRepository;

    @Transactional
    public int updateExpiredAds(AdStatus newStatus, AdStatus currentStatus, LocalDateTime now) {
        return adRepository.updateExpiredAds(newStatus, currentStatus, now);
    }

    @Async("adTaskExecutor")
    @Transactional
    public CompletableFuture<Integer> deleteExpiredAdsBatchAsync(List<Long> batchToRemove, int currentBatchNum) {
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
            adRepository.deletePostAds(batchToRemove);
            log.info("Successfully deleted batch number {}", currentBatchNum);
            return CompletableFuture.completedFuture(batchToRemove.size());
        } catch (Exception e) {
            log.error("Error deleting batch number {}: {}", currentBatchNum, e.getMessage(), e);
            CompletableFuture<Integer> failureFuture = new CompletableFuture<>();
            failureFuture.completeExceptionally(new RuntimeException("Error deleting ads in batch " + currentBatchNum));
            return failureFuture;
        }
    }

    @Transactional(readOnly = true)
    public Page<Long> findAdIdsByStatus(AdStatus adStatus, Pageable pageRequest) {
        return adRepository.findAdIdsByStatus(adStatus, pageRequest);
    }
}

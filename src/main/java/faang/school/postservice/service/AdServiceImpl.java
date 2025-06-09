package faang.school.postservice.service;

import faang.school.postservice.model.ad.AdStatus;
import faang.school.postservice.repository.adapter.AdRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdServiceImpl implements AdService {
    private final AdRepositoryAdapter adRepositoryAdapter;

    @Value("${scheduler.delete-expired-ads.batch-size}")
    private int deleteBatchSize;

    @Override
    @Transactional
    public void updateExpiredAds() {
        log.info("Starting updateExpiredAds() in AdServiceImpl... ");
        LocalDateTime now = LocalDateTime.now();
        try {
            int updateCount = adRepositoryAdapter.updateExpiredAds(AdStatus.EXPIRED, AdStatus.ACTIVE, now);
            if (updateCount > 0) {
                log.info("Updated {} expired ads to status {}", updateCount, AdStatus.EXPIRED);
            } else {
                log.info("No ads were updated. No expired ads found.");
            }
        } catch (Exception e) {
            log.error("Error updating expired ads: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating expired ads", e);
        }
    }

    @Override
    public void deleteExpiredAdsInBatches() {
        log.info("Starting deleteExpiredAdsInBatches() in AdServiceImpl...");
        AtomicInteger totalDeleted = new AtomicInteger(0);
        AtomicInteger batchSubmissionNumber = new AtomicInteger(0);
        List<CompletableFuture<Integer>> batchExecutionFutures = new ArrayList<>();
        try {
            if (Thread.currentThread().isInterrupted()) {
                log.warn("Thread was interrupted, stopping deleteExpiredAdsAsync.");
                return;
            }
            List<Long> adIdsForCurrentBatch;
            do {
                Pageable pageRequest = PageRequest.of(
                        batchSubmissionNumber.getAndIncrement(),
                        deleteBatchSize,
                        Sort.by("id").ascending()
                );
                adIdsForCurrentBatch = adRepositoryAdapter
                        .findAdIdsByStatus(AdStatus.EXPIRED, pageRequest)
                        .getContent();
                if (!adIdsForCurrentBatch.isEmpty()) {
                    int currentBatchNum = batchSubmissionNumber.get();
                    List<Long> batchToProcess = new ArrayList<>(adIdsForCurrentBatch);
                    log.info("deleteExpiredAdsInBatches: Processing batch {} with {} ads",
                            currentBatchNum, batchToProcess.size());
                    CompletableFuture<Integer> batchFuture =
                            adRepositoryAdapter.deleteExpiredAdsBatchAsync(batchToProcess, currentBatchNum);
                    batchExecutionFutures.add(batchFuture);
                }
            } while (!adIdsForCurrentBatch.isEmpty() && !Thread.currentThread().isInterrupted());
            if (batchExecutionFutures.isEmpty()) {
                log.info("deleteExpiredAdsInBatches: No expired ads found to delete.");
                return;
            }
            CompletableFuture<Void> allBatchesCompletionFuture = CompletableFuture.allOf(
                    batchExecutionFutures.toArray(new CompletableFuture[0])
            );
            processBatchResults(allBatchesCompletionFuture, batchExecutionFutures, totalDeleted);
        } catch (Exception e) {
            log.error("Critical error while deleting expired ads: {}", e.getMessage(), e);
            throw new RuntimeException("error while deleting expired ads", e);
        }
    }

    private void processBatchResults(CompletableFuture<Void> allBatchesCompletionFuture,
                                     List<CompletableFuture<Integer>> batchExecutionFutures,
                                     AtomicInteger totalDeleted) {
        allBatchesCompletionFuture.handle((result, ex) -> {
            batchExecutionFutures.forEach(future -> {
                if (future.isDone() && !future.isCompletedExceptionally()) {
                    try {
                        totalDeleted.addAndGet(future.getNow(0));
                    } catch (Exception e) {
                        log.warn("Error retrieving result from batch future: {}", e.getMessage(), e);
                    }
                }
            });
            if (ex != null) {
                log.error("Error during batch processing: {}", ex.getMessage(), ex);
                throw new RuntimeException(String.format("Error during batch processing: %s %s",
                        ex.getMessage(), ex.getClass()));
            } else {
                if (totalDeleted.get() > 0) {
                    log.info("Successfully deleted {} expired ads in total.", totalDeleted.get());
                } else {
                    log.info("No expired ads were deleted.");
                }
                return null;
            }
        });
    }
}

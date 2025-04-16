package faang.school.postservice.service.Ad;

import faang.school.postservice.config.ad.AdDeletionProperties;
import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.validator.AdValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final AdValidator adValidator;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final AdDeletionProperties adDeletionProperties;

    @Override
    @Transactional
    public void removeExpiredAds() {
        List<Long> expiredAdIds = adRepository.findExpiredAdIds(LocalDateTime.now());
        if (expiredAdIds.isEmpty()) {
            log.info("No expired ads found to delete");
            return;
        }
        adValidator.validateAdIds(expiredAdIds);
        List<List<Long>> batches = partitionList(expiredAdIds);
        List<CompletableFuture<Void>> futures = submitDeleteTasks(batches);
        waitForCompletion(futures);
    }

    @Override
    @Transactional
    public void deleteAdById(long id) {
        adValidator.validateAdId(id);
        try {
            adRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting ad with id {}: {}", id, e.getMessage());
        }
    }

    private <T> List<List<T>> partitionList(List<T> list) {
        return ListUtils.partition(list, adDeletionProperties.getBatchSize());
    }

    private List<CompletableFuture<Void>> submitDeleteTasks(List<List<Long>> batches) {
        if (batches.isEmpty()) {
            return Collections.emptyList();
        }
        return batches.stream()
                .map(batch -> CompletableFuture.runAsync(() -> {
                    deleteBatch(batch);
                }, taskExecutor))
                .toList();
    }

    private void deleteBatch(List<Long> batch) {
        try {
            adRepository.deleteByIds(batch);
        } catch (Exception e) {
            log.error("Error while deleting batch of size {}: {}", batch.size(), e.getMessage());
        }
    }

    private void waitForCompletion(List<CompletableFuture<Void>> futures) {
        if (futures.isEmpty()) {
            return;
        }
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allOf.get();
        } catch (InterruptedException e) {
            log.error("Batch deletion interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error("Error executing batch deletion: {}", e.getCause().getMessage());
        }
    }
}

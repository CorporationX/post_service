package faang.school.postservice.service.Ad;

import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.validator.AdValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final AdValidator adValidator;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Value("${ad.deletion.batchsize}")
    private int batchSize;

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
        List<Future<Object>> futures = submitDeleteTasks(batches);
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
        return ListUtils.partition(list, batchSize);
    }

    private List<Future<Object>> submitDeleteTasks(List<List<Long>> batches) {
        return batches.stream()
                .map(batch -> taskExecutor.submit(() -> {
                    deleteBatch(batch);
                    return null;
                }))
                .toList();
    }

    private void deleteBatch(List<Long> batch) {
        try {
            adRepository.deleteByIds(batch);
        } catch (Exception e) {
            log.error("Error while deleting batch of size {}: {}", batch.size(), e.getMessage());
        }
    }

    private void waitForCompletion(List<Future<Object>> futures) {
        for (Future<Object> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                log.error("Error executing batch deletion: {}", e.getCause().getMessage());
            } catch (InterruptedException e) {
                log.error("Batch deletion interrupted: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}

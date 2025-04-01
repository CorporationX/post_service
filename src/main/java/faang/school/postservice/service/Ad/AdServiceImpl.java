package faang.school.postservice.service.Ad;

import faang.school.postservice.repository.ad.AdRepository;
import faang.school.postservice.validator.AdValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final AdValidator adValidator;
    private final ExecutorService executorService;

    @Value("${ad.deletion.batchsize}")
    private int batchSize;

    @Override
    public void removeExpiredAds() {
        List<Long> expiredAdIds = adRepository.findExpiredAdIds(LocalDateTime.now());
        if (expiredAdIds.isEmpty()) {
            log.info("No expired ads found to delete");
            return;
        }
        adValidator.validateAdIds(expiredAdIds);
        List<List<Long>> batches = partitionList(expiredAdIds, batchSize);
        List<Future<?>> futures = submitDeleteTasks(executorService, batches);
        waitForCompletion(futures);
    }

    @Override
    public void deleteAdById(long id) {
        adValidator.validateAdId(id);
        try {
            adRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting ad with id {}: {}", id, e.getMessage());
        }
    }

    private <T> List<List<T>> partitionList(List<T> list, int batch) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batch) {
            partitions.add(list.subList(i, Math.min(i + batch, list.size())));
        }
        return partitions;
    }

    private List<Future<?>> submitDeleteTasks(ExecutorService executorService, List<List<Long>> batches) {
        List<Future<?>> futures = new ArrayList<>();
        for (List<Long> batch : batches) {
            futures.add(executorService.submit(() -> {
                try {
                    adRepository.deleteByIds(batch);
                } catch (Exception e) {
                    log.error("Error while deleting batch {}: {}", batch, e.getMessage());
                }
            }));
        }
        return futures;
    }

    private void waitForCompletion(List<Future<?>> futures) {
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (ExecutionException | InterruptedException e) {
                log.error("Error executing batch deletion: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }
}

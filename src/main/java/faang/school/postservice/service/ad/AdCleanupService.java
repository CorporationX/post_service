package faang.school.postservice.service.ad;

import faang.school.postservice.repository.ad.AdRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AdCleanupService {

    private final AdRepository adRepository;
    private final ThreadPoolTaskExecutor executor;

    public AdCleanupService(
            AdRepository adRepository,
            ThreadPoolTaskExecutor expiredAdTaskExecutor
    ) {
        this.adRepository = adRepository;
        this.executor = expiredAdTaskExecutor;
    }

    @Value("${cleanup.chunk-size}")
    private int chunkSize;

    public void clearExpiredAds() {
        log.info("Initiating expired ad cleanup");
        List<Long> expiredAdIds = adRepository.findExpiredAdIds();
        if (expiredAdIds.isEmpty()) {
            log.info("No expired ads found.");
            return;
        }
        log.info("Splitting list of expired ads into chunks for parallel processing");
        List<List<Long>> chunks = partitionList(expiredAdIds);
        log.info("Removing expired ads from DB");
        chunks.forEach((chunk) -> executor.execute(() -> {
            try {
                adRepository.deleteAllById(chunk);
            } catch (Exception e) {
                log.error("Deletion failed: ", e);
            }
        }));
    }

    private List<List<Long>> partitionList(List<Long> list) {
        List<List<Long>> parts = new ArrayList<>((list.size() + chunkSize - 1) / chunkSize);
        for (int i = 0; i < list.size(); i += chunkSize) {
            parts.add(list.subList(i, Math.min(list.size(), i + chunkSize)));
        }
        return parts;
    }
}
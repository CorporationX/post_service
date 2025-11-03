package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;

    @Value("${cleanup.chunk-size}")
    private int chunkSize;

    public void clearExpiredAds() {

        List<Long> expiredAdIds = new ArrayList<>();
        for (Ad ad : adRepository.findAll()) {
            if (ad.getEndDate().isBefore(LocalDateTime.now()) || ad.getAppearancesLeft() == 0) {
                expiredAdIds.add(ad.getId());
            }
        }
        if (expiredAdIds.isEmpty()) {
            log.info("No expired ads found.");
            return;
        }

        List<List<Long>> chunks = partitionList(expiredAdIds, chunkSize);

        ExecutorService executor = Executors.newFixedThreadPool(chunks.size());

        chunks.forEach((chunk) -> executor.execute(() -> adRepository.deleteAllById(chunk)));

        gracefulShutdown(executor);
    }

    private List<List<Long>> partitionList(List<Long> list, int chunkSize) {
        List<List<Long>> parts = new ArrayList<>((list.size() + chunkSize - 1) / chunkSize);
        for (int i = 0; i < list.size(); i += chunkSize) {
            parts.add(list.subList(i, Math.min(list.size(), i + chunkSize)));
        }
        return parts;
    }

    private void gracefulShutdown(ExecutorService exec) {
        exec.shutdown();

        try {
            if (!exec.awaitTermination(5, TimeUnit.MINUTES)) {
                log.warn("Deletion task is taking too long ...");
                exec.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Deletion task has been interrupted ...");
            throw new RuntimeException(e);
        }
        log.info("Deletion task has been completed!");
        exec.shutdownNow();
    }
}

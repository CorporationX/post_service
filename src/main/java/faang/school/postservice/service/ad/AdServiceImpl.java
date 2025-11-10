package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    @Value("${ads.max-delete-per-thread:100}")
    private int maxDeletePerThread;

    private final AdRepository adRepository;
    private final ExecutorService deleteAdsPool;

    @Transactional
    @Override
    public void deleteExpiredAds() {
        List<Long> expiredAds = adRepository.findAll()
                .stream()
                .filter(ad -> ad.getEndDate().isBefore(LocalDateTime.now()) || ad.getAppearancesLeft() == 0)
                .map(Ad::getId)
                .toList();

        log.info("Found {} expired ads: {}", expiredAds.size(), expiredAds);

        List<List<Long>> parts = new ArrayList<>();
        for (int i = 0; i < expiredAds.size(); i += maxDeletePerThread) {
            int end = Math.min(i + maxDeletePerThread, expiredAds.size());
            parts.add(expiredAds.subList(i, end));
        }

        List<CompletableFuture<Void>> futures = parts.stream()
                .map(part -> CompletableFuture.runAsync(
                        () -> {
                            log.info("Deleting ads: {}", part);
                            adRepository.deleteAllById(part);
                        },
                        deleteAdsPool
                ))
                .toList();

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(30, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.error("Deletion timed out. {}", e.getMessage());
            futures.forEach(future -> future.cancel(true));
        } catch (Exception e) {
            log.error("Error during deletion. {}", e.getMessage());
        }

        log.info("{} expired ads have been deleted", expiredAds.size());
    }
}
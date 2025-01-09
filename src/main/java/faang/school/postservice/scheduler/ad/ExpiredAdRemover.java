package faang.school.postservice.scheduler.ad;

import faang.school.postservice.service.ad.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredAdRemover {

    private final AdService adService;

    @Value("${scheduler.expired-ad.batch-size}")
    private int batchSize;

    @Scheduled(cron = "scheduler.expired-ad.cron")
    @Async("clearAdsThreadPool")
    public void deleteExpiredAds() {

        List<Long> expiredAdIds = adService.findExpiredAdIds();

        if (!expiredAdIds.isEmpty()) {
            log.info("Starting deleting...");
            List<CompletableFuture<Void>> adDeleteFuture = ListUtils
                    .partition(expiredAdIds, batchSize).stream()
                    .map(this::delete)
                    .toList();

            CompletableFuture.allOf(adDeleteFuture.toArray(new CompletableFuture[0]))
                    .thenRun(() -> log.info("Expired ads were deleted."));

        } else {
            log.info("No ads to delete.");
        }
    }

    public CompletableFuture<Void> delete(List<Long> ids) {
        return CompletableFuture.runAsync(() -> {
            adService.deleteAdByIds(ids);
        });
    }

}


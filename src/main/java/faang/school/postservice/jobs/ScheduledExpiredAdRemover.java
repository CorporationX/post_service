package faang.school.postservice.jobs;

import faang.school.postservice.service.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Scheduled(cron = "${scheduler.update-expired-ads.cron}")
    public void markExpiredAds() {
        log.info("Scheduler 'markExpiredAds' started");
        try {
            adService.updateExpiredAds();
            log.info("Scheduler 'markExpiredAds' finished successfully");
        } catch (Exception e) {
            log.error("Scheduler 'markExpiredAds' failed with an error:", e);
            throw new RuntimeException("Scheduler 'markExpiredAds': error while marking expired ads");
        }
    }

    @Scheduled(cron = "${scheduler.delete-expired-ads.cron}")
    public void scheduledExpiredAdCleanUp() {
        log.info("Scheduler 'scheduledExpiredAdCleanUp' started");
        try {
            adService.deleteExpiredAdsInBatches();
            log.info("Scheduler 'scheduledExpiredAdCleanUp' " +
                    "successfully delegated expired ad batches to async executors");

        } catch (Exception e) {
            log.error("Scheduler 'scheduledExpiredAdCleanUp' failed with an error:", e);
            throw new RuntimeException("Scheduler 'scheduledExpiredAdCleanUp': error while deleting expired ads");
        }
    }
}

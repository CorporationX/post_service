package faang.school.postservice.scheduler;

import faang.school.postservice.service.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Value("${scheduler.expired-ad.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${scheduler.expired-ad.cron}")
    public void removeExpiredAd() {
        log.debug("Scheduled removing expired ads - Started");
        adService.removeExpiredAds(batchSize);
        log.info("Scheduled removing expired ads - Finished");
    }
}
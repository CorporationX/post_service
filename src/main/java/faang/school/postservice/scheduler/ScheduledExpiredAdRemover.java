package faang.school.postservice.scheduler;

import faang.school.postservice.service.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    public final AdService adService;

    @Scheduled(cron = "${scheduler.expired-ad.cron}")
    public void removeExpiredAd() {
        log.debug("Scheduled removing expired ads - Started");
        adService.removeExpiredAds();
        log.info("Scheduled removing expired ads - Finished");
    }
}
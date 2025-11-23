package faang.school.postservice.scheduler;

import faang.school.postservice.service.ad.AdCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {

    private final AdCleanupService adCleanupService;

    @Scheduled(cron = "${cleanup.cron}")
    public void clearExpiredAds() {
        adCleanupService.clearExpiredAds();
    }
}

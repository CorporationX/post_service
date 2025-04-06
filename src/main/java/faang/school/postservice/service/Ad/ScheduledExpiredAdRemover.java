package faang.school.postservice.service.Ad;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {

    private final AdService adService;

    @Scheduled(cron = "${ad.expired.cron-expired-post-ad-deletion}")
    public void scheduledRemoveExpiredAds() {
        adService.removeExpiredAds();
    }
}

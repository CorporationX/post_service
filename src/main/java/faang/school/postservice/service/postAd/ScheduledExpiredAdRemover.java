package faang.school.postservice.service.postAd;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {

    private final AdService adService;

    @Scheduled(cron = "${post.ad-remover.expired.scheduler.cron}")
    public void deleteExpiredAds() {
        adService.deleteExpiredAd();
    }
}

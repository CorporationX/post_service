package faang.school.postservice.scheduler;

import faang.school.postservice.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Scheduled(cron = "${post.ad-remover.scheduler.cron}")
    public void removeExpiredAds() {
        adService.removeExpiredAds();
    }
}

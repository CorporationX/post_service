package faang.school.postservice.scheduler;

import faang.school.postservice.service.ad.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    @Value("${post.ad-remover.max-list-size}")
    private int maxListSize;
    private final AdService adService;

    @Scheduled(cron = "${post.ad-remover.scheduler.cron}")
    public void removeExpiredAds() {
        adService.removeExpiredAds(maxListSize);
    }
}

package faang.school.postservice.scheduler;

import faang.school.postservice.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Value("${post.ad-remover.max-list-size}")
    private int maxListSize;

    @Scheduled(cron = "${post.ad-remover.scheduler.cron}")
    public void removeExpiredAds() {
        adService.removeExpiredAds(maxListSize);
    }
}

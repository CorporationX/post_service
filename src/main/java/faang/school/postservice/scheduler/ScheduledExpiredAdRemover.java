package faang.school.postservice.scheduler;

import faang.school.postservice.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Async
    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void remove() {
        adService.removeExpiredAds();
    }
}

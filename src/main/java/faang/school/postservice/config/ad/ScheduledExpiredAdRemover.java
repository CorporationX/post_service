package faang.school.postservice.config.ad;

import faang.school.postservice.service.ad.AdServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduledExpiredAdRemover {
    private final AdServiceImpl adService;

    @Scheduled(cron = "${post.ad.time-to-remove}")
    public void removeAd() {
        adService.removeAd();
    }
}
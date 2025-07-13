package faang.school.postservice.bjs_77879;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {

    private final AdService adService;

    @Scheduled(cron = "${ad-cleanup.cron}")
    public void removeExpiredAds() {
        adService.deleteExpiredAds();
    }
}




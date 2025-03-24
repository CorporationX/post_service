package faang.school.postservice.scheduled;

import faang.school.postservice.service.ad.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Value("${scheduling.cron}")
    private String cronExpression;

    @Scheduled(cron = "${scheduling.cronExpression}")
    public void removingExpiredAdvertisements() {
        adService.removingExpiredAds();
    }
}

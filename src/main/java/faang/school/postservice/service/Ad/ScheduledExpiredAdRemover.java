package faang.school.postservice.service.Ad;

import faang.school.postservice.config.ad.ExpiredAdProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {

    private final AdService adService;
    private final ExpiredAdProperties expiredAdProperties;

    @Scheduled(cron = "#{@expiredAdProperties.cronExpiredPostAdDeletion}")
    public void scheduledRemoveExpiredAds() {
        adService.removeExpiredAds();
    }
}

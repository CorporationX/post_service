package faang.school.postservice.schedule;

import faang.school.postservice.service.ad.AdServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdServiceImpl adService;

    @Scheduled(cron = "${cron.delete-expired-ads-cron}")
    public void deleteExpiredAds() {
        adService.deleteExpiredAds();
    }
}
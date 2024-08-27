package faang.school.postservice.config;


import faang.school.postservice.service.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Value("${ad.ad-deleted.scheduler.batchSize.size}")
    private int batchSize;

    @Scheduled(cron = "${ad.ad-deleted.scheduler.cron}")
    public void removingExpiredAdvertisements(){
        adService.deleteInactiveAds(batchSize);
    }
}

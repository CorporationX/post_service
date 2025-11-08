package faang.school.postservice.config.ad;

import faang.school.postservice.service.ad.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExpiredAdRemoverScheduler {
    private final AdService adService;

    @Scheduled(cron = "${post.ad.time-to-remove}")
    public void removeAd() {
        log.debug("Start scheduled remove Ads");
        adService.removeAds();
    }
}
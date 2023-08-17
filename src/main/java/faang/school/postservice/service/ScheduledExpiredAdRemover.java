package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Scheduled(cron = "${ad-remover.cron}")
    public void deleteOverdueAds() {
        log.info("Ad remover job started");
        try {
            adService.deleteOverdueAds();
        } catch (Exception e) {
            log.error("Error while ad remover job", e);
        }
        log.info("Ad remover job ended");
    }
}

package faang.school.postservice.scheduled;

import faang.school.postservice.service.adservice.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledExpiredAdRemover {
    public final AdService adService;

    @Scheduled(cron = "${scheduled.cron.removeAds}")
    public void deleteAdsWhichEndPaidPeriod() {
        log.info("Запуск джобы на проверку завершенных рекламных кампаний");
        adService.deleteAdsWhichEndPaidPeriod();
        log.info("Завершение выполнения джобы на проверку завершенных рекламных кампаний");
    }
}

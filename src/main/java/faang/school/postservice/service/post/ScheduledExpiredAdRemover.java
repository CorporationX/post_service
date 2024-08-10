package faang.school.postservice.service.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Scheduled(cron = "${cron-expression}")
    public void scheduledDeleteExpiredAds() {
        log.info("Стартовала задача по удалению истёкших рекламных объявлений.");
        adService.deleteExpiredAds();
    }
}

package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Scheduled(cron = "${ad-remover.cron}")
    @Retryable(maxAttemptsExpression = "${ad-remover.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${ad-remover.retry.maxDelay}"))
    public void deleteOverdueAds() {
        log.info("Ad remover job started");
        adService.deleteOverdueAds();
        log.info("Ad remover job ended");
    }

    @Recover
    void recover(ExhaustedRetryException e) {
        log.error("Error while ad remover job!", e);
    }
}

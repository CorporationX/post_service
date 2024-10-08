package faang.school.postservice.scheduler;

import faang.school.postservice.service.ModerationPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationScheduler {

    private final ModerationPostService moderationPostService;

    @Scheduled(cron = "${post.verify.scheduler.cron}")
    @Retryable(
            retryFor = RuntimeException.class,
            maxAttemptsExpression = "${post.verify.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${post.verify.retry.delay}")
    )
    public void moderationPosts() {
        log.info("Moderation posts started");
        moderationPostService.moderationPosts();
        log.info("Moderation posts finished");
    }
}

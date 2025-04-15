package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Component
public class ModerationScheduler {
    private final PostService postService;
    private final ExecutorService moderationExecutor;

    @Value("${moderation.threads}")
    private int threadSize;


    @Scheduled(cron = "${moderation.job.cron}")
    public void runModerationJob() {
        try {
            postService.moderatePosts();

            moderationExecutor.shutdown();

            if (!moderationExecutor.awaitTermination(1, TimeUnit.HOURS)) {
                log.warn("Forcing executor shutdown after timeout");
                moderationExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Moderation job interrupted", e);
            Thread.currentThread().interrupt();
            moderationExecutor.shutdownNow();
        }
    }
}

package faang.school.postservice.scheduler.post;

import faang.school.postservice.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@EnableScheduling
public class ScheduledPostPublisherImpl implements ScheduledPostPublisher {

    private final PostService postService;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public ScheduledPostPublisherImpl(PostService postService) {
        this.postService = postService;
    }

    @Scheduled(
            cron = "${scheduler.cron.expression:0 * * * * *}",
            zone = "${scheduler.cron.zone:GMT}")
    public void publishScheduledPosts() {
        if (!running.compareAndSet(false, true)) {
            log.warn("Scheduled publish skipped — previous execution still running");
            return;
        }
        log.info("Scheduled publish started at {} on thread {}",
                java.time.LocalDateTime.now(),
                Thread.currentThread().getName());
        log.info("Scheduled publish completed successfully");
        try {
            postService.publishScheduledPosts();
        } catch (Exception e) {
            log.error("Scheduled publish failed", e);
        } finally {
            running.set(false);
        }
    }
}


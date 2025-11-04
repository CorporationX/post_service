package faang.school.postservice.scheduled;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostCorrecter {

    private final PostService postService;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Scheduled(cron = "${post-correcter.cron}")
    public void correctPosts() {
        if (!isRunning.compareAndSet(false, true)) {
            log.warn("Post correction is already running, skipping");
            return;
        }

        try {
            log.info("Starting scheduled post correction");
            postService.processTextChecking();
        } finally {
            isRunning.set(false);
            log.info("Finished post correction");
        }
    }
}
package faang.school.postservice.service.moderation.schedule;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationScheduler {

    private final PostService postService;

    @Scheduled(cron = "${moderation.cron-expression}")
    public void moderatePostsJob() {
        log.info("=== Starting moderation job ===");
        try {
            long startTime = System.currentTimeMillis();

            postService.moderateAllUnverifiedPosts();

            long duration = System.currentTimeMillis() - startTime;
            log.info("=== Moderation job completed in {} ms ===", duration);
        } catch (Exception e) {
            log.error("Moderation job failed", e);
        }
    }

}

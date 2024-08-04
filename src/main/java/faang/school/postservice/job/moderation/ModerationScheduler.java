package faang.school.postservice.job.moderation;

import faang.school.postservice.exception.ModerationSchedulerException;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModerationScheduler {

    private final PostService postService;

    @Scheduled(cron = "${post.moderation.scheduler.cron}")
    void runModerationNewPosts() {
        log.info("Starting moderation of new posts...");
        try {
            postService.moderateNewPosts();
            log.info("Moderation of new posts completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred during moderation of new posts: ", e);
            throw new ModerationSchedulerException(e.getMessage(), e);
        }
    }
}

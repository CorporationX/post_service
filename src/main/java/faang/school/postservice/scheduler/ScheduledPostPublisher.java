package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledPostPublisher {

    private final PostService postService;

    @Scheduled(cron = "${scheduler.cron-expression}")
    public void publishScheduledPosts() {
        log.info("Starting scheduled post publication job");
        try {
            postService.publishScheduledPosts();
            log.info("Finished scheduled post publication job");
        } catch (Exception e) {
            log.error("Failed to publish scheduled posts", e);
        }
    }
}
package faang.school.postservice.schedule;


import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduledPostPublisher {

    private final PostService postService;

    @Scheduled(cron = "${app.scheduled-posts.cron:0 * * * * *}")
    public void publishScheduledPosts() {
        log.info("Launching the scheduler for publishing scheduled posts");
        try {
            postService.publishScheduledPosts();
        } catch (Exception e) {
            log.error("Error when publishing scheduled posts", e);
        }
    }
}

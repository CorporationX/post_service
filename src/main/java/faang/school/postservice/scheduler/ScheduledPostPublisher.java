package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private final PostService postService;
    @Value("${scheduler.publish-cron}")
    private String publishCron;

    @Scheduled(cron = "${scheduler.publish-cron}")
    public void publicScheduledPostJob() {
        postService.publishScheduledPosts();
    }
}

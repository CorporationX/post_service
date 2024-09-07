package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void scheduledPostPublish() {
        postService.publishScheduledPosts();
    }
}

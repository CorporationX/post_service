package faang.school.postservice.scheduled;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private PostService postService;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }
}

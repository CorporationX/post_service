package faang.school.postservice.publisher;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduledPostPublisher {

    private final PostService postService;

    @Scheduled(cron = "${post.publisher.scheduler.cron-fix-rate}")
    public void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }

}

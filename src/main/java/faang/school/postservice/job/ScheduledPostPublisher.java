package faang.school.postservice.job;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Evgenii Malkov
 */
@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {

    private final PostService postService;

    @Scheduled(cron = "${post.publisher.scheduler.cron:0 0/1 * 1/1 * ?}")
    public void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }
}

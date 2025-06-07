package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "${cron.each-minute}")
    private void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }
}

package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Data
@Component
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "0 * * * * *")
    private void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }
}

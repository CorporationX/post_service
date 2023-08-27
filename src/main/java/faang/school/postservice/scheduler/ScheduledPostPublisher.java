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
    @Value("${spring.scheduler.publisher.partitionSize}")
    private int partitionSize;

    @Scheduled(cron = "${spring.scheduler.publisher.cron}")
    public void publishPosts() {
        postService.publishScheduledPosts(partitionSize);
    }


}

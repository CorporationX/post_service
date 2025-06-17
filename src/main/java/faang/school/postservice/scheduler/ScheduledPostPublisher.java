package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduledPostPublisher {
    private final PostSchedulerService postSchedulerService;

    @Scheduled(cron = "${project.post-publisher.cron}")
    private void publishScheduledPosts() {
        postSchedulerService.publishScheduledPosts();
    }
}

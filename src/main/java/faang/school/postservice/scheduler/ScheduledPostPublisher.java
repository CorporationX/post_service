package faang.school.postservice.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduledPostPublisher {
    private final SchedulerService schedulerService;

    @Scheduled(cron = "${cron.project.each-minute}")
    private void publishScheduledPosts() {
        schedulerService.publishScheduledPosts();
    }
}

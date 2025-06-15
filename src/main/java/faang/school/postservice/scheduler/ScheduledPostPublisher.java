package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScheduledPostPublisher {
    private final PostSchedulerService postSchedulerService;

    @Scheduled(cron = "${cron.project.each-minute}")
    private void publishScheduledPosts() {
        postSchedulerService.publishScheduledPosts();
    }
}

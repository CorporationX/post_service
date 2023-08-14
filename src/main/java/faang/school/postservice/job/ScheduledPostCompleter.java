package faang.school.postservice.job;

import faang.school.postservice.service.ScheduledPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostCompleter {

    private final ScheduledPostService scheduledPostService;

    @Scheduled(fixedDelayString = "${schedule.time_interval}")
    @Async("taskExecutor")
    public void completeScheduledPosts() {
        scheduledPostService.completeScheduledPosts();
    }
}

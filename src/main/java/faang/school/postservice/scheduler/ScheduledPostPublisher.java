package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "0 0/1 * * * *")
    public void postPublisher() {

    }
}

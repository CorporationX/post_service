package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostCorrecter {
    private final PostService postService;

    @Scheduled(cron = "${scheduler.post-correcter.cron}")
    public void correctPost() {
        postService.correctPost();
    }
}
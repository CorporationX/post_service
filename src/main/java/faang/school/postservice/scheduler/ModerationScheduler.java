package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModerationScheduler {
    private final PostService postService;
    @Async
    @Scheduled(cron = "${post.moderator.scheduler.cron}")
    public void moderate(){
        postService.verifyContent();
    }
}

package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModerationScheduler {

    public final PostService postService;

    @Scheduled(cron = "${post.inspector.scheduler.cron}")
    public void inspectPosts() {
        postService.moderateAll();
    }
}

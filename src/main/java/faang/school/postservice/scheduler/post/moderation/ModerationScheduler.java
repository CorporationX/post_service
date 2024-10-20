package faang.school.postservice.scheduler.post.moderation;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class ModerationScheduler {
    private final PostService postService;

    @Scheduled(cron = "${post.moderation.scheduler.cron}")
    public void runModeration() {
        postService.moderatePosts();
    }
}

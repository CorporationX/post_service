package faang.school.postservice.moderation;

import faang.school.postservice.service.ModerationService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModerationScheduler {
    private final PostServiceImpl postService;

    @Scheduled(cron = "${post.moderation.scheduler.cron}")
    public void moderation() {
        postService.checkPostsWithBadWord();
    }
}

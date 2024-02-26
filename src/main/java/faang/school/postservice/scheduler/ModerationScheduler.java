package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModerationScheduler {
    private final PostService postService;
    @Scheduled(cron = "${post_moderation.time}")
    public void moderatePosts() {
        postService.moderatePosts();
    }
}

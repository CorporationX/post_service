package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.PostService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ModerationScheduler {
    private final PostService postService;

    @Scheduled(cron = "${moderation.post.badWords.cron}")
    public void moderatePosts() {
        postService.moderatePosts();
    }
}
package faang.school.postservice.moderation.starter;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModerationScheduler {

    private final PostService postService;

    @Scheduled(cron = "${moderationScheduler.cron}")
    @Async("postServiceThreadPool")
    public void moderatePosts() {
        postService.moderatePosts();
    }
}

package faang.school.postservice.scheduler.post;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduledPostCorrecter {
    private final PostService postService;

    @Scheduled(cron = "${post-corrector.cron-expression}")
    public void correctPosts() {
        postService.correctUnpublishedPosts();
    }
}
package faang.school.postservice.config.post;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduledPostPublisher {
    private final PostService postService;

    @Async("scheduledPostsExecutor")
    @Scheduled(cron = "${scheduled-post-publisher.comments.cron}")
    public void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }
}
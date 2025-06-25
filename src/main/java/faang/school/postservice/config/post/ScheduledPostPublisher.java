package faang.school.postservice.config.post;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "${scheduled-post-publisher.comments.cron}")
    public void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }
}
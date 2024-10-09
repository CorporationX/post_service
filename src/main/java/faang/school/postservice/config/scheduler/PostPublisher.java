package faang.school.postservice.config.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostPublisher {

    private final ScheduledPostPublisher scheduledPostPublisher;
    private final PostService postService;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void sendPostToPublisher() {
        List<Post> posts = postService.getAllPostsNotPublished();
        scheduledPostPublisher.publish(posts.toString());
    }
}

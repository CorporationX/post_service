package faang.school.postservice.moderation;

import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostModerator {
    private final PostService postService;
    private final int batchSize;
    private final ThreadPoolTaskExecutor executor;

    @Autowired
    public PostModerator(PostService postService,
                            @Value("${post.moderation.batchSize}") int batchSize,
                            ThreadPoolTaskExecutor executor) {
        this.postService = postService;
        this.batchSize = batchSize;
        this.executor = executor;
    }

    @Scheduled(cron = "${post.moderation.scheduler.cron}")
    public void moderatePosts() {
        List<Post> unverifiedPosts = postService.getUnverifiedPosts();

        for (int i = 0; i < unverifiedPosts.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, unverifiedPosts.size());
            List<Post> postsBatch = unverifiedPosts.subList(i, endIndex);
            executor.submit(() -> postService.processPostsBatch(postsBatch));
        }
    }
}

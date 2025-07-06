package faang.school.postservice.moderation.model.post;

import faang.school.postservice.config.moderation.CommentsModerationConfiguration;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component

public class PostModeration {

    private final PostService postService;
    private final ThreadPoolTaskExecutor executor;
    private final CommentsModerationConfiguration configuration;
    private final PostContentChecker postContentChecker;

    public PostModeration(PostService postService, @Qualifier("taskExecutor") ThreadPoolTaskExecutor executor,
                          CommentsModerationConfiguration configuration, PostContentChecker postContentChecker) {
        this.postService = postService;
        this.executor = executor;
        this.configuration = configuration;
        this.postContentChecker = postContentChecker;
    }

    @Scheduled(cron = "#{@commentsModerationConfiguration.cron}")
    public void startExecutor() {
        List<List<Post>> postList = searchUnverifiedPosts();

        for (List<Post> batch : postList) {
            executor.submit(() -> {
                postContentChecker.verifyPostsBatch(batch);
            });
        }
    }

    public List<List<Post>> searchUnverifiedPosts() {
        List<Post> postList = postService.getNotVerifiedPosts();
        int batchSize = configuration.getBatchSize();

        return ListUtils.partition(postList, batchSize);
    }
}
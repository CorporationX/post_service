package faang.school.postservice.scheduledexecutor;

import faang.school.postservice.scheduledexecutor.deleting.ScheduledCommentDeleterImpl;
import faang.school.postservice.scheduledexecutor.deleting.ScheduledLikeDeleterImpl;
import faang.school.postservice.scheduledexecutor.deleting.ScheduledPostDeleterImpl;
import faang.school.postservice.scheduledexecutor.publishing.ScheduledCommentPublisherImpl;
import faang.school.postservice.scheduledexecutor.publishing.ScheduledLikePublisherImpl;
import faang.school.postservice.scheduledexecutor.publishing.ScheduledPostPublisherImpl;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ScheduledTaskExecutorConfig {

    private final PostService postService;
    private final LikeService likeService;
    private final CommentService commentService;

    private static final String POST = "POST";
    private static final String LIKE = "LIKE";
    private static final String COMMENT = "COMMENT";
    private static final String DELETING = "DELETING";
    private static final String PUBLISHING = "PUBLISHING";

    @Bean
    public Map<List<String>, ScheduledTaskExecutor> scheduledTaskExecutors() {
        return Map.of(
                List.of(POST, DELETING), new ScheduledPostDeleterImpl(postService),
                List.of(LIKE, DELETING), new ScheduledLikeDeleterImpl(likeService),
                List.of(COMMENT, DELETING), new ScheduledCommentDeleterImpl(commentService),
                List.of(POST, PUBLISHING), new ScheduledPostPublisherImpl(postService),
                List.of(LIKE, PUBLISHING), new ScheduledLikePublisherImpl(likeService),
                List.of(COMMENT, PUBLISHING), new ScheduledCommentPublisherImpl(commentService)
        );
    }
}

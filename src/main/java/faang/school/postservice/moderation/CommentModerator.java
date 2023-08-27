package faang.school.postservice.moderation;

import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentModerator {

    private final CommentService commentService;
    private final int batchSize;
    private final ThreadPoolTaskExecutor executor;

    @Autowired
    public CommentModerator(CommentService commentService,
                            @Value("${comment.moderation.batchSize}") int batchSize,
                            ThreadPoolTaskExecutor executor) {
        this.commentService = commentService;
        this.batchSize = batchSize;
        this.executor = executor;
    }

    @Scheduled(cron = "${comment.moderation.scheduler.cron}")
    public void moderateComments() {
        List<Comment> unverifiedComments = commentService.getUnverifiedComments();

        for (int i = 0; i < unverifiedComments.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, unverifiedComments.size());
            List<Comment> commentsBatch = unverifiedComments.subList(i, endIndex);
            executor.submit(() -> commentService.processCommentsBatch(commentsBatch));
        }

        executor.shutdown();
    }
}

package faang.school.postservice.moderation;

import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final CommentService commentService;
    @Value("${comment.moderation.scheduler.batchSize}")
    private final int batchSize;
    private final ThreadPoolTaskExecutor executor;

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

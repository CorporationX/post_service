package faang.school.postservice.moderation;

import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final CommentService commentService;
    @Value("${comment.moderation.scheduler.batchSize}")
    private final int batchSize;

    @Scheduled(cron = "${comment.moderation.scheduler.cron}")
    public void moderateComments() {
        List<Comment> unverifiedComments = commentService.getUnverifiedComments();
        ExecutorService executor = Executors.newFixedThreadPool(unverifiedComments.size() / batchSize + 1);

        for (int i = 0; i < unverifiedComments.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, unverifiedComments.size());
            List<Comment> commentsBatch = unverifiedComments.subList(i, endIndex);
            executor.submit(() -> commentService.processCommentsBatch(commentsBatch));
        }

        executor.shutdown();
    }
}

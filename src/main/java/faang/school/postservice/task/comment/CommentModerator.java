package faang.school.postservice.task.comment;

import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentModerator {

    private final CommentService commentService;

    @Value("${comment.verify-batch}")
    private int verifyBatch;

    @Scheduled(cron = "${comment.verify-cron}")
    public void verifyComment() {
        List<Comment> comments = commentService.getUnverifiedComments();
        log.info("Verify comment count: {}", comments.size());
        for (int i = 0; i < comments.size(); i += verifyBatch) {
            int endIndex = Math.min(i + verifyBatch, comments.size());
            commentService.verifyComments(comments.subList(i, endIndex));
        }
    }
}

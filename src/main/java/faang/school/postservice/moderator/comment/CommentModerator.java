package faang.school.postservice.moderator.comment;

import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final CommentService commentService;

    @Async
    @Scheduled(cron = "${post.moderator.scheduler.moderateComment.cron}")
    public void moderateComment() {
        commentService.moderateComment();
    }
}

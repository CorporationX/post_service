package faang.school.postservice.moderator.comment;

import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final CommentService commentService;

    @Scheduled(cron = "0 0 0 * * *")
    public void moderateComment() {
        commentService.moderateComment();
    }
}

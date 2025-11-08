package faang.school.postservice.config.moderator;

import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentModerator {

    private final CommentService commentService;

    @Scheduled(cron = "${app.moderation.cron}")
    public void moderateComments() {
        commentService.moderateNewComments();
    }
}

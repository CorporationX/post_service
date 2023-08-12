package faang.school.postservice.service.moderation;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final CommentService commentService;

    @Scheduled(cron = "${comment.moderator.scheduler.cron}")
    public void moderateComments() {
        commentService.moderateComments();
    }
}

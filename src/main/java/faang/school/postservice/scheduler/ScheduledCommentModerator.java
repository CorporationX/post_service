package faang.school.postservice.scheduler;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledCommentModerator {
    private final CommentService commentService;

    @Scheduled(cron = "${scheduler.moderation.comment.time}")
    public void moderate() {
        commentService.moderateComment();
    }
}
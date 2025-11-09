package faang.school.postservice.job.moderator;

import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentModerator {

    private final CommentService commentService;

    @Scheduled(fixedDelayString = "${app.moderation.time:}")
    public void moderateComments() {
        commentService.moderateNewComments();
    }
}

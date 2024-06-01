package faang.school.postservice.moderator.comment.starter;

import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final CommentService commentService;

    @Scheduled(cron = "${commentModeratorCron}")
    public void commentModerator(){
        commentService.moderateComment();
    }
}

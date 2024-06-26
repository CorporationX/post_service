package faang.school.postservice.moderator.comment.starter;

import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentModeratorStarter {
    private final CommentService commentService;

    @Async("postServicePool")
    @Scheduled(cron = "${commentModeratorCron}")
    public void commentModerator(){
        commentService.moderateComment();
    }
}

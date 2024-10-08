package faang.school.postservice.scheduler;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentModerator {

    private final CommentService commentService;

    @Scheduled(cron = "${post.comments.scheduler.cron}")
    public void moderateCommentaries(){
        commentService.moderateComments();
    }
}

package faang.school.postservice.utils;


import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final CommentService commentService;

    @Scheduled(cron = "${moderation.comments.cron}")
    public void moderateComments() {
        commentService.moderateComments().subscribe();
    }
}
package faang.school.postservice.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentBanner {
    private final CommentService commentService;

    @Scheduled(cron = "${moderation.comments.cron}")
    public void commentBanner() {
        commentService.banUsersForComments();
    }
}

package faang.school.postservice.task.comment;

import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommenterBanner {

    private final CommentService commentService;

    @Scheduled(cron = "${comment.user-ban-cron}")
    public void checkUsersForBan() {
        commentService.findUsersToBan();
    }
}

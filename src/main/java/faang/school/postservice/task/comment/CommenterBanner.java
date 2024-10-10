package faang.school.postservice.task.comment;

import faang.school.postservice.exception.comment.UserBanException;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommenterBanner {

    private final CommentService commentService;

    @Value("${comment.user-ban-comment-limit}")
    private int banCommentLimit;

    @Scheduled(cron = "${comment.user-ban-cron}")
    @Retryable(retryFor = {UserBanException.class}, backoff = @Backoff(delay = 5000))
    public void banUsers() {
        try {
            commentService.banUsersWithObsceneCommentsMoreThan(banCommentLimit);
        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw new UserBanException("Fail ban user", e);
        }
    }
}

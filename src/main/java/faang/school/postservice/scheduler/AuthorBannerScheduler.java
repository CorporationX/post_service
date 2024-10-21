package faang.school.postservice.scheduler;

import faang.school.postservice.exception.comment.UserBanException;
import faang.school.postservice.service.post.PostService;
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
public class AuthorBannerScheduler {

    private final PostService postService;

    @Value("${post.user-ban.post-limit}")
    private int banPostLimit;

    @Scheduled(cron = "${post.user-ban.cron}")
    @Retryable(retryFor = {UserBanException.class}, backoff = @Backoff(delay = 5000))
    public void banAuthors() {
        try {
            postService.banAuthorsWithUnverifiedPostsMoreThan(banPostLimit);
        } catch (Exception e) {
            log.error("Failed to ban authors: {}", e.getMessage(), e);
            throw new UserBanException("Failed to ban authors", e);
        }
    }
}
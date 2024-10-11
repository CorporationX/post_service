package faang.school.postservice.scheduler;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class CommenterBanner {

    private final CommentService commentService;

    @Value("${post.commenter-banner.unverified-comments-limit}")
    private int unverifiedCommentsLimit;

    @Scheduled(cron = "${post.commenter-banner.scheduler.cron}")
    public void scheduleCommentersBanCheck() {
        commentService.commentersBanCheck(unverifiedCommentsLimit);
    }
}

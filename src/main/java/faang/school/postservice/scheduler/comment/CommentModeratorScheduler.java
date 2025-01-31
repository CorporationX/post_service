package faang.school.postservice.scheduler.comment;

import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
@Slf4j
public class CommentModeratorScheduler {
    private final CommentService commentService;

    @Async("moderationCommentThreadPool")
    @Transactional
    @Scheduled(cron = "${moderation.cron}", zone = "${moderation.zone}")
    public void moderateCommentsToOffensiveContent() {
        log.info("Started checking profanities for comments");
        commentService.checkProfanities();
        log.info("Finished checking profanities for comments");
    }
}

package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ModerationScheduler {
    private static final Logger log = LoggerFactory.getLogger(ModerationScheduler.class);
    private final PostService postService;
    private final CommentService commentService;

    @Scheduled(cron = "${moderation.cron}")
    public void runModeration() {
        log.info("Moderation scheduler started");
        int moderatedCount = commentService.moderateComments();
        log.info("Moderation job completed. Moderated {} comments.", moderatedCount);
        postService.moderatePosts();
        log.info("Moderation scheduler finished");
    }
}

package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommentModerator {
    private final CommentService commentService;

    @Scheduled(cron = "${moderation.schedule}")
    public void runModerationJob() {
        int moderatedCount = commentService.moderateComments();
        log.info("Moderation job completed. Moderated {} comments.", moderatedCount);
    }
}
package faang.school.postservice.moderation;

import faang.school.postservice.service.ModerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModerationScheduler {
    private final ModerationService moderationService;

    @Scheduled(cron = "${post.moderation.scheduler.cron}")
    public void moderation() {
        moderationService.checkPostsWithBadWord();
    }
}

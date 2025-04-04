package faang.school.postservice.job;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final CommentService commentService;

    @Scheduled(cron = "${moderation.cron}")
    public void runModerationJob() {
        commentService.moderateUnverifiedComments();
    }
}
package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.ContentModerator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentContentModerationScheduler {
    private final ContentModerator contentModerator;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void start() {
        contentModerator.moderateComment();
    }
}

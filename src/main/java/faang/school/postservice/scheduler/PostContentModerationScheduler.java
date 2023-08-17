package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.ContentModerator;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostContentModerationScheduler {
    private final ContentModerator contentModerator;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void start() {
        contentModerator.moderate();
    }
}

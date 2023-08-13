package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationScheduler {

    private final PostService postService;

    @Scheduled(cron = "${post.moderation.scheduler.cron}")
    public void doPostModeration() {
        log.info("Post moderation is starting in ModerationScheduler at {}", LocalDateTime.now());
        postService.doPostModeration();
    }
}

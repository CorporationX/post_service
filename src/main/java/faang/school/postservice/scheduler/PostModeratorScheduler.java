package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostModeratorScheduler implements Moderator {

    private final PostService postService;

    @Scheduled(cron = "${app.scheduling.daily-midnight-cron}")
    @Override
    public void startModerate() {
        log.info("Post moderation started");
        postService.moderatePosts();
        log.info("All posts have been moderated");
    }
}

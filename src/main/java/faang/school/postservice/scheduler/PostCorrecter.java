package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecter {

    private final PostService postService;

    @Value("${scheduler.post-correction-cron}")
    private String cronExpression;

    @Scheduled(cron = "${scheduler.post-correction-cron}")
    public void runCorrectionJob() {
        log.info("Launch spell check of posts by cron: {}", cronExpression);
        postService.correctUnpublishedPosts();
        log.info("Spell check for posts completed");
    }
}
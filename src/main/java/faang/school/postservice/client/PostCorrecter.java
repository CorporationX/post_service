package faang.school.postservice.client;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecter {

    private final PostService postService;

    @Scheduled(cron = "${daily.cron.check-posts}")
    public void checkPosts() {
        log.info("Starting PostCorrectionJob...");
        postService.correctUnpublishedPosts();
        log.info("PostCorrectionJob finished.");
    }
}


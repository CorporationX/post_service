package faang.school.postservice.config;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostCorrecterConfig {
    private final PostService postService;

    @Scheduled(cron = "${posts.correction.cron}")
    public void sendPostsForChecking() {
        log.info("Submitting posts for review started");
        postService.sendPostsForChecking();
    }
}

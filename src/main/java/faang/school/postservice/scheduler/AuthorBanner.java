package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorBanner {
    private final PostService postService;

    @Scheduled(cron = "${post.auto_banner.scheduler.cron}")
    public void processUnverifiedPosts() {
        postService.checkAndBanAuthors();
        log.info("scheduled processUnverifiedPosts method completed");
    }
}
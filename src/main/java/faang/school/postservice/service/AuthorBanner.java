package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorBanner {
    private final PostService postService;

    @Scheduled(cron = "${banned.scheduler.cron}")
    public void postBanner() {
        postService.checkUserAndBannedForPost();
    }
}
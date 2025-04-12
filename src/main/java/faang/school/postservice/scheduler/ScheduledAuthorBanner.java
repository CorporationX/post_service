package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledAuthorBanner {

    private final PostService postService;

    @Scheduled(cron = "${cron.author-ban}")
    @Async("authorBannerPool")
    public void checkAuthorsPostsVerification() {
        log.debug("Start check authors posts verification");
        postService.checkAuthorsPostsVerification();
    }
}

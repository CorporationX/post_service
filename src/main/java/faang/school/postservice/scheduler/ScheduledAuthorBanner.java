package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledAuthorBanner {

    private final PostService postService;

    @Scheduled(cron = "${cron.author-ban}")
    @Async("authorBannerPool")
    public void checkAuthorsPostsVerification() {
        postService.checkAuthorsPostsVerification();
    }
}

package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledAuthorBanner {
    private final PostService postService;

    @Scheduled (cron = "${spring.scheduler.userBannerPublisher.cron}")
    public void publishAuthorBanner() {
        postService.publishAuthorBanner();
    }
}
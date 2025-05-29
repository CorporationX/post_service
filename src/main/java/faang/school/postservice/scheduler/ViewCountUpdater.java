package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountUpdater {

    private final PostService postService;

    @Async("viewCountUpdaterExecutor")
    @Scheduled(cron = "${cron.update-view-count}")
    public void updateViewCountOnDatabase() {
        log.info("Updating view count started");
        postService.updateViewCount();
        log.info("Updating view count completed");
    }
}

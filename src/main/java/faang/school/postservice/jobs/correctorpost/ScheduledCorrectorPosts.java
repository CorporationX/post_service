package faang.school.postservice.jobs.correctorpost;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledCorrectorPosts {

    private final ScheduledCorrectorPostsAsync scheduledCorrectorPostsAsync;

    @Scheduled(cron = "${app.correction.cron}")
    public void correctingSpellingOfPosts() {
        log.info("Scheduled job correcter post content started");
        scheduledCorrectorPostsAsync.correctingSpellingOfPosts();
    }
}
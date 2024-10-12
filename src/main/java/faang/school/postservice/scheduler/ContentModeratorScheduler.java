package faang.school.postservice.scheduler;

import faang.school.postservice.service.moderator.ModeratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContentModeratorScheduler {

    private final ModeratorService moderatorService;

    @Scheduled(initialDelayString = "${moderate.comments.initialDelay}",
            fixedDelayString = "${moderate.comments.fixedDelay}")
    public void moderateComments() {
        log.info("moderateComments() - start");
        moderatorService.moderateCommentsContent();
        log.info("moderateComments() - finish");
    }
}

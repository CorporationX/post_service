package faang.school.postservice.scheduler;

import faang.school.postservice.service.spellcheck.PostCorrectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecter {

    private final PostCorrectionService correctionService;

    @Scheduled(cron = "${scheduler.post-correction-cron}")
    public void runCorrectionJob() {
        log.info("Launch spell check of posts");
        correctionService.correctAllUnpublishedPosts();
        log.info("Spell check for posts completed");
    }
}

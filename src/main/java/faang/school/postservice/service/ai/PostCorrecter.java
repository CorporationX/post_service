package faang.school.postservice.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecter {

    private final AiPostCorrectionService aiPostCorrectionService;

    @Scheduled(cron = "${post-correcter.cron}")
    public void runCorrectionJob() {
        aiPostCorrectionService.correctDraftPosts();
        log.info("Draft post correction job completed");
    }
}


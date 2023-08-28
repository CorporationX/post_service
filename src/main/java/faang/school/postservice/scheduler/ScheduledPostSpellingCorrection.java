package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostSpellingCorrection {
    private final PostService postService;

    @Scheduled(cron = "${spring.scheduler.spellChecker.cron}")
    public void correctionSpelling() {
        postService.correctionSpelling();
    }
}

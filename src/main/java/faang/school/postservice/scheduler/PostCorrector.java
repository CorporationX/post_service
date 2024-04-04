package faang.school.postservice.scheduler;

import faang.school.postservice.corrector.ContentCorrector;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCorrector {
    private final ContentCorrector contentCorrector;

    @Scheduled(cron = "${post.post-spell-check.scheduler.cron}")
    public void spellCheckTextInPosts() {
        contentCorrector.spellCheckTextInPosts();
    }
}

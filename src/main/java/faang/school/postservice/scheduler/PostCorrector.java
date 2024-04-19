package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.SpellCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCorrector {
    private final SpellCheckService spellCheckService;

    @Scheduled(cron = "${post.post-spell-check.scheduler.cron}")
    public void spellCheckTextInPosts() {
        spellCheckService.spellCheckTextInPosts();
    }
}

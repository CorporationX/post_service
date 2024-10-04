package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCorrector {
    private final PostService postService;

    @Scheduled(cron = "${post.spell-corrector.scheduler.cron}")
    public void correctPosts() {
        postService.correctAllDraftPosts();
    }
}

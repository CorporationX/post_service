package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCorrecter {
    private final PostService postService;

    @Scheduled(cron = "${spell-checker.scheduler.cron}")
    public void correctPostsOnSchedule() {
        postService.correctSpellingInUnpublishedPosts();
    }
}

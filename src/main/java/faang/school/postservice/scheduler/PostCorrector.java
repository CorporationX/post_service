package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCorrector {

    private final PostService postService;

    @Scheduled(cron = "${scheduler.post-corrector.cron}")
    public void postTextCorrection() {
        postService.postTextCorrection();
    }

}

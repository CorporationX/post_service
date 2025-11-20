package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostCorrecter {
    private final PostService postService;

    @Scheduled(cron = "${app.cron.check-spelling}")
    public void checkSpelling() {
        postService.checkSpellingWithAI();
    }
}

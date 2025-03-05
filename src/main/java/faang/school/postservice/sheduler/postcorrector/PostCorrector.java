package faang.school.postservice.sheduler.postcorrector;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class PostCorrector {
    private final PostService postService;

    @Scheduled(cron = "${auto-start-syntax.cron}")
    @Retryable(retryFor = {IOException.class, InterruptedException.class}, maxAttempts = 5, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void startCheckingPosts() throws IOException, InterruptedException {
        postService.checkingPostForErrors();
    }
}
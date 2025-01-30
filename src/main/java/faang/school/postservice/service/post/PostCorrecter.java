package faang.school.postservice.service.post;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class PostCorrecter {
    private final PostService postService;

    @Scheduled(cron = "${cron.spelling-check}")
    public void sendPostToSpellingCheck() {
        postService.sendPostToSpellingCheck();
    }
}
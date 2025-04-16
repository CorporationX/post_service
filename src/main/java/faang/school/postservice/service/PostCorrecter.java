package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCorrecter {

    private final PostService postService;

    @Scheduled(cron = "${post-corrector.cron.every-day-at-three-pm}")
    public void correctUnpublishedPosts() {
        postService.correctUnpublishedPosts();
    }
}

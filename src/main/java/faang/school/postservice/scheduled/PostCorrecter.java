package faang.school.postservice.scheduled;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
//for PR
@Component
@Slf4j
@RequiredArgsConstructor
public class PostCorrecter {

    private final PostService postService;

    @Scheduled(cron = "${post-correcter.cron}")
    public void correctPosts() {
        log.info("Starting scheduled post correction");
        postService.processTextChecking();
        log.info("Finished post correction");
    }
}

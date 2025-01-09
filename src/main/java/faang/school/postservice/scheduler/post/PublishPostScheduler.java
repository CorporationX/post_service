package faang.school.postservice.scheduler.post;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Slf4j
@Component
public class PublishPostScheduler {
    private final PostService postService;

    @Async("publishPostThreadPool")
    @Scheduled(cron = "${scheduled-publication.cron}")
    @Transactional
    public void publishScheduledPosts() {
        log.info("Started publishing scheduled posts");
        postService.publishScheduledPosts();
        log.info("Finished publishing scheduled posts");
    }
}

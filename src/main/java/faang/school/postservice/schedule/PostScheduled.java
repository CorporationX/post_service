package faang.school.postservice.schedule;

import faang.school.postservice.service.PostCorrecterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostScheduled {

    private final PostCorrecterService postCorrecterService;

    @Scheduled(cron = "${post.correct.content.scheduler.cron}")
    public void correctContentPosts() {
        log.info("Starting scheduled post correction");
        postCorrecterService.correctAllPosts();
        log.info("Finishing scheduled post correction");
    }
}

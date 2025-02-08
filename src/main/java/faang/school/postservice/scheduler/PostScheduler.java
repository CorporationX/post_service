package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostScheduler {
    private final PostService postService;

    @Scheduled(cron = "${scheduling.publishing-cron}")
    public void publishingPostsOnSchedule() {
        int count = postService.publishingPostsOnSchedule();
        log.debug("Published posts on schedule: {}", count);
    }
}

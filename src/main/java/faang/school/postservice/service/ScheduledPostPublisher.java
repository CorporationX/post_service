package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "0 * * * * *")
    public void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }
}

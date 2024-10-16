package faang.school.postservice.service.impl;

import faang.school.postservice.service.PostService;
import faang.school.postservice.service.ScheduledPostPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledPostPublisherImpl implements ScheduledPostPublisher {

    private final PostService postService;

    @Override
    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void publish() {
        log.info("Start publishing scheduled posts...");
        postService.publishScheduledPosts();
    }
}

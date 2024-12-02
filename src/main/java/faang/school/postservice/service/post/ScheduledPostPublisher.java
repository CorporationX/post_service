package faang.school.postservice.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(fixedRateString = "${task-scheduler.fixedRate.in.milliseconds}")
    public void postPublish(){
        postService.publishScheduledPosts();
    }
}
package faang.school.postservice.job;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {

    private final PostService postService;

//    @Scheduled(fixedRateString = "${schedule.time_interval}")
//    public void publishScheduledPosts() {
//        postService.publishScheduledPosts();
//    }
}

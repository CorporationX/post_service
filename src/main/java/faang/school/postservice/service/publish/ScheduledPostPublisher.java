package faang.school.postservice.service.publish;


import faang.school.postservice.service.PostService;
import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Data
public class ScheduledPostPublisher {

    private final PostService postService;

    @Scheduled(cron = "${ad.expired.cron-one-minute}")
    public void publishSchedulePost() {
        postService.publishSchedulePosts();
    }
}

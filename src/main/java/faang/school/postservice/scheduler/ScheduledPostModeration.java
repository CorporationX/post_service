package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostModeration {

    private final PostService postService;

    @Scheduled(cron = "${moderation.schedule.cron}")
    public void moderatePosts() {
        postService.moderatePosts();
    }
}

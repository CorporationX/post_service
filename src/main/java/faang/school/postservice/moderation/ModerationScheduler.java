package faang.school.postservice.moderation;

import faang.school.postservice.service.PostService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModerationScheduler {

    private final PostService postService;

    @Scheduled(cron = "${post.moderation.scheduler.cron}")
    public void moderateContent() {
        postService.moderatePostsContent();
    }
}

package faang.school.postservice.job;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorBanner {
    private final PostService postService;

    @Scheduled(cron = "${post.publisher.ad-remover.scheduler.cron}")
    public void ScheduledAuthorBan() {
        postService.banUser();
    }
}

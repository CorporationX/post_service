package faang.school.postservice.job;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommenterBanner {
    private final CommentService commentService;

    @Scheduled(cron = "${post.publisher.ad-remover.scheduler.cron}")
    public void ScheduledAuthorBan() {
        commentService.banUser();
    }
}

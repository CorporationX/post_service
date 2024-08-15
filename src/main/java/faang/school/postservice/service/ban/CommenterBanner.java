package faang.school.postservice.service.ban;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommenterBanner {
    private final CommentService commentService;

    @Scheduled(cron = "${banned.scheduler.cron}")
    public void commenterBanner() {
        commentService.checkUserAndBannedForComment();
    }
}
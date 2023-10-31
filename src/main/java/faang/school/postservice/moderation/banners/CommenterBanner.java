package faang.school.postservice.moderation.banners;

import faang.school.postservice.service.CommentBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CommenterBanner {
    private final CommentBanService commentBanService;
    @Scheduled(cron = "${comment.ban.scheduler.cron}")
    public void banCommenters() {
        commentBanService.findCommentersAndPublishBanEvent();
    }
}

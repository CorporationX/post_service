package faang.school.postservice.moderation.banners;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CommenterBanner {
    private final PostService postService;
    @Scheduled(cron = "${comment.ban.scheduler.cron}")
    public void banCommenters() {
        postService.findCommentersAndPublishBanEvent();
    }
}

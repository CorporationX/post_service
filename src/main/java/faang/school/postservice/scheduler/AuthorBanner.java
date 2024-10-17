package faang.school.postservice.scheduler;

import faang.school.postservice.publis.publisher.UserBanMessagePublisher;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorBanner {
    private final UserBanMessagePublisher userBanMessagePublisher;
    private final PostService postService;

    @Scheduled(cron = "${post.user_ban.scheduler.cron}")
    public void sendBanAuthorsIdsToPublisher() {
        List<Long> banAuthorsIds = postService.getAuthorsWithExcessVerifiedFalsePosts();
        userBanMessagePublisher.publish(banAuthorsIds.toString());
        log.info("Send authorsIds to ban: " + banAuthorsIds);
    }
}

package faang.school.postservice.scheduler.ban;

import faang.school.postservice.event.ban.UserBanEvent;
import faang.school.postservice.publisher.ban.UserBanMessagePublisher;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorBanner {

    private final PostService postService;
    private final UserBanMessagePublisher userBanMessagePublisher;

    @Value("${post.author-banner.unverified-posts-limit}")
    private int unverifiedPostLimit;

    @Scheduled(cron = "${post.author-banner.schedule.cron}")
    public void publishUsersBanMessage() {
        postService
                .findAuthorsWithUnverifiedPosts(unverifiedPostLimit)
                .stream()
                .map(UserBanEvent::new)
                .forEach(userBanMessagePublisher::publish);
    }
}

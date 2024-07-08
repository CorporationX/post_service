package faang.school.postservice.service;

import faang.school.postservice.dto.event.UserBanEvent;
import faang.school.postservice.publisher.UserBanPublisher;
import faang.school.postservice.service.post.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthorBanner {

    private final PostService postService;
    private final UserBanPublisher userBanPublisher;

    @Autowired
    public AuthorBanner(PostService postService, UserBanPublisher userBanPublisher) {
        this.postService = postService;
        this.userBanPublisher = userBanPublisher;
    }

    @Scheduled(cron = "${post.banner.scheduler.cron}")
    public void checkAndBanAuthors() {
        List<Long> authorIds = postService.getAuthorIdsToBan();
        UserBanEvent event = new UserBanEvent(authorIds);
        userBanPublisher.sendUserBanEvent(event);
    }
}

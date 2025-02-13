package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import faang.school.postservice.service.kafka.BanProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AuthorBanner {

    private final PostService postService;
    private final BanProducer producer;

    @Scheduled(cron = "${scheduler.user_ban.cron}")
    public void banUsersWithUnverifiedPosts() {
        List<Long> banUsers = postService.getUsersForBanWithUnverifiedPosts();
        banUsers.forEach(producer::sendUsersToBan);
    }
}

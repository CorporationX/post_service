package faang.school.postservice.scheduler;

import faang.school.event.UserBanEvent;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.kafka.BanProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AuthorBanner {

    private final PostService postService;
    private final BanProducer producer;

    @Value("${spring.kafka.topics.user-ban-topic.name}")
    private String userBanTopicName;

    @Scheduled(cron = "${scheduler.user_ban.cron}")
    public void banUsersWithUnverifiedPosts() {
        List<Long> banUsers = postService.getUsersForBanWithUnverifiedPosts();
        banUsers.stream()
                .map((id) -> new UserBanEvent(id, false))
                .forEach((event) -> producer.sendUsersToBan(userBanTopicName, event));
    }
}

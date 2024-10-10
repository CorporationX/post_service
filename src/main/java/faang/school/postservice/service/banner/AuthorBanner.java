package faang.school.postservice.service.banner;

import faang.school.postservice.publisher.RedisMessagePublisher;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorBanner {
    private final PostService postService;
    private final RedisMessagePublisher redisMessagePublisher;

    @Scheduled(cron = "${redis.banner.schedule}")
    public void publishingUsersForBan() {
        List<Long> userIdsForBan = postService.findUserIdsForBan();
        userIdsForBan
                .forEach(id -> redisMessagePublisher.publish(String.valueOf(id)));
    }
}
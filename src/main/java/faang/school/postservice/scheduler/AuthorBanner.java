package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorBanner {

    private final RedisTemplate<String, List<Long>> redisTemplate;
    private final PostService postService;

    @Value("${spring.data.redis.channels.user-service.name}")
    private String channel;

    @Scheduled(cron = "${post.ban-user.scheduler.cron}")
    public void banUser() {
        List<Long> violatorIds = postService.getAuthorsWithMoreFiveUnverifiedPosts();
        log.info("Send ban user event {}", violatorIds);
        redisTemplate.convertAndSend(channel, violatorIds);
    }
}

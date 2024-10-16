package faang.school.postservice.scheduler;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorBannerScheduler {

    private final RedisTemplate<String, List<Long>> redisTemplate;
    private final PostService postService;
    private final RedisProperties redisProperties;

    @Scheduled(cron = "${post.ban-user.scheduler.cron}")
    public void banUser() {
        List<Long> violatorIds = postService.getAuthorsWithMoreFiveUnverifiedPosts();
        String channel = redisProperties.getChannels().get("user-service");

        log.info("Send ban user event {}", violatorIds);
        redisTemplate.convertAndSend(channel, violatorIds);
    }
}

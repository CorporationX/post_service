package faang.school.postservice.scheduler;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorBanner {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;
    private final PostService postService;

    @Scheduled(cron = "${schedule.user_ban.ban_interval}")
    public void banAuthors() {
        List<Long> banAuthorsIds = postService.getAuthorsWithExcessVerifiedFalsePosts();
        log.info("Send authorsIds to ban: " + banAuthorsIds);
        redisTemplate.convertAndSend(redisProperties.getUserBanChannelName(), banAuthorsIds);
    }
}

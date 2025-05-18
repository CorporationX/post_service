package faang.school.postservice.service.redis;

import faang.school.postservice.service.hashtags.HashtagRedisWarmUpService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RedisHeatService {

    @Bean
    CommandLineRunner warmUpHashtagService(HashtagRedisWarmUpService redisWarmUpService) {
        return args -> redisWarmUpService.warmUpCache();
    }
}

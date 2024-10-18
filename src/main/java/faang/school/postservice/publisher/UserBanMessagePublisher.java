package faang.school.postservice.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserBanMessagePublisher extends AbstractMessagePublisher {
    @Value("${redis.banner.topic}")
    private String bannerTopic;

    public UserBanMessagePublisher(StringRedisTemplate stringRedisTemplate) {
        super(stringRedisTemplate);
    }

    @Override
    protected String getTopicName() {
        return bannerTopic;
    }
}

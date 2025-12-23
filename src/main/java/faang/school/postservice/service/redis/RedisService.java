package faang.school.postservice.service.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.PostForFeedDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RedisService {
    private static final String KEY_PREFIX_BY_POST_FEED = "post_feed_by_subscriber_";

    @Value("${spring.data.redis.post-feed.maximum-feed:500}")
    private Integer maxPostsPerSubscriber;

    private final RedisTemplate<String, Object> redisTemplatePostFeed;
    private final ObjectMapper objectMapperPostFeed;

    public RedisService(@Qualifier("redisTemplateForKafkaPostConsumer") RedisTemplate<String, Object> redisTemplatePostFeed,
                        ObjectMapper objectMapperPostFeed) {
        this.redisTemplatePostFeed = redisTemplatePostFeed;
        this.objectMapperPostFeed = objectMapperPostFeed;
    }

    public void savePostForFeed(PostForFeedDto postForFeedDto) {
        List<Long> subscriberIds = postForFeedDto.subscriberIds();
        for (Long id : subscriberIds) {
            String key = KEY_PREFIX_BY_POST_FEED + id;

          saveToRedis(key, postForFeedDto.postId());
        }
    }

    public List<Long> getSubscriberIdsForPost(Long subscriberIds) {
        Object value = redisTemplatePostFeed.opsForZSet().range(KEY_PREFIX_BY_POST_FEED + subscriberIds, 0, -1);
        if (value != null) {
            return objectMapperPostFeed.convertValue(value, List.class);
        } else {
            return new ArrayList<>();
        }
    }

    private void saveToRedis(String key, Long postId) {

        redisTemplatePostFeed.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(key);

                Long currentSize = operations.opsForZSet().size(key);
                operations.multi();

                operations.opsForZSet().add(key, postId, -System.currentTimeMillis());

                if (currentSize != null && currentSize >= maxPostsPerSubscriber) {
                    operations.opsForZSet().removeRange(key, maxPostsPerSubscriber, -1);
                }
                try {
                    operations.exec();
                } catch (Exception e) {
                    operations.discard();
                    throw e;
                }
                return null;
            }
        });
    }
}

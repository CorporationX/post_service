package faang.school.postservice.service;

import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.repository.PostViewCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewConsumerImpl implements KafkaPostViewConsumer {

    private final StringRedisTemplate redisTemplate;
    private final PostViewCacheRepository postViewCacheRepository;

    @Value("${app.cache.posts.key-prefix}")
    private String postKeyPrefix;

    @Value("${app.cache.posts.value}")
    private String postValue;

    @KafkaListener(topics = "${app.cache.authors.key-prefix}", groupId = "${app.cache.consumer.group-id}")
    @Override
    public void consume(PostViewEvent postViewEvent) {
        log.info("Received event: {}", postViewEvent);

        String postKey = postKeyPrefix + postViewEvent.getPostId();

        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(@NotNull RedisOperations operations) throws DataAccessException {
                operations.watch(postKey);
                if(Boolean.TRUE.equals(operations.hasKey(postKey))) {
                    operations.multi();
                    try {
                        operations.opsForZSet().incrementScore(postKey, postValue, 1);
                        return operations.exec();
                    } catch (Exception e) {
                        operations.discard();
                        log.error("Concurrent modification error for post {}", postViewEvent.getPostId(), e);
                    }
                }
                return null;
            }
        });

    }
}

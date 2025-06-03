package faang.school.postservice.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest(classes = {TestConfig.class})
public class KafkaLikeConsumerTest extends TestContainer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void shouldSkipDuplicateLike() {
        long postId = 1L;
        long userId = 1L;
        String postKey = "post:" + postId;
        String likesKey = "likes:" + postId;
        String userIdStr = String.valueOf(userId);

        redisTemplate.opsForHash().put(postKey, "likeCount", "1");
        redisTemplate.opsForHash().put(postKey, "exists", "true");
        redisTemplate.opsForSet().add(likesKey, userIdStr);

        kafkaTemplate.send("t.like", String.format("{\"postId\":%d,\"userId\":%d}", postId, userId));

        await()
                .pollInterval(3, TimeUnit.SECONDS)
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Boolean hasKey = redisTemplate.hasKey(postKey);
                    Long likeAdded = redisTemplate.opsForSet().isMember(likesKey, userIdStr) ? 1L : 0L;
                    Long likeCount = Long.valueOf(redisTemplate.opsForHash().get(postKey, "likeCount").toString());

                    assert hasKey;
                    assert likeAdded == 1L;
                    assert likeCount == 1L;
                });
    }

    @Test
    void shouldSkipLikeForNonExistentPost() {
        long postId = 1L;
        long userId = 1L;
        String postKey = "post:" + postId;
        String likesKey = "likes:" + postId;
        String userIdStr = String.valueOf(userId);

        kafkaTemplate.send("t.like", String.format("{\"postId\":%d,\"userId\":%d}", postId, userId));

        await()
                .pollInterval(3, TimeUnit.SECONDS)
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Boolean hasKey = redisTemplate.hasKey(postKey);
                    Long likeAdded = redisTemplate.opsForSet().isMember(likesKey, userIdStr) ? 1L : 0L;

                    assert !hasKey;
                    assert likeAdded == 0L;
                });
    }
}
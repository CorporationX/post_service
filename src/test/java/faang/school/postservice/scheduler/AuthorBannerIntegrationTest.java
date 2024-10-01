package faang.school.postservice.scheduler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.RedisTestConfig;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.relational.core.sql.In;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "spring.config.location=classpath:application.yaml")
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import({RedisTestConfig.class})
class AuthorBannerIntegrationTest {

    @Autowired
    private AuthorBanner authorBanner;

    @MockBean
    private PostService postService;

    @Autowired
    private RedisTemplate<String, List<Long>> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;

    private RedisServer redisServer;

    @Value("${spring.data.redis.port}")
    private Integer port;

    @Value("${spring.data.redis.host}")
    private String host;

    @BeforeEach
    void setUp() {
        redisServer = new RedisServer(port);
        redisServer.start();

        System.setProperty("spring.redis.host", host);
        System.setProperty("spring.redis.port", port.toString());

        redisMessageListenerContainer.start();
        assertTrue(redisMessageListenerContainer.isRunning());
    }

    @AfterEach
    void tearDown() {
        redisMessageListenerContainer.stop();
        redisServer.stop();
    }

    @Test
    void testBanUserEventSentToRedis() throws InterruptedException {
        List<Long> violatorIds = List.of(1L, 2L, 3L);
        when(postService.getAuthorsWithMoreFiveUnverifiedPosts()).thenReturn(violatorIds);

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<List<Long>> result = new AtomicReference<>();

        redisMessageListenerContainer.addMessageListener((message, pattern) -> {
            try {
                List<Long> ids = objectMapper.readValue(message.getBody(), new TypeReference<>() {
                });
                result.set(ids);
            } catch (IOException e) {
                fail();
            } finally {
                latch.countDown();
            }
        }, new ChannelTopic("ban_user_channel"));

        authorBanner.banUser();
        boolean messageReceived = latch.await(30, TimeUnit.SECONDS);

        assertTrue(messageReceived);
        assertNotNull(result.get());
        assertTrue(violatorIds.containsAll(result.get()));
    }
}
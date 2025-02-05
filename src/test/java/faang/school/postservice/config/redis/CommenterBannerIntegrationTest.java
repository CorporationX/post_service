package faang.school.postservice.config.redis;

import faang.school.postservice.scheduler.CommenterBanner;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CommenterBannerIntegrationTest {
    @Autowired
    private CommenterBanner commenterBanner;

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @MockBean
    private CommentService commentService;

    @Value("${spring.data.redis.channels.ban-channel.name}")
    private String banChannelName;

    private RedisMessageListenerContainer container;
    private BlockingQueue<Object> messages;
    private Jackson2JsonRedisSerializer<Object> serializer;

    @BeforeEach
    void setup() {
        messages = new LinkedBlockingQueue<>();
        serializer = new Jackson2JsonRedisSerializer<>(Object.class);

        container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener((Message message, byte[] pattern) -> {
            Object deserialized = serializer.deserialize(message.getBody());
            messages.add(deserialized);
        }, new ChannelTopic(banChannelName));
        container.afterPropertiesSet();
        container.start();
    }

    @AfterEach
    void tearDown() {
        if (container != null) {
            container.stop();
        }
    }

    @Test
    public void whenRunBannerTask_thenMessagePublished() throws InterruptedException {
        List<Long> expectedAuthorIds = Arrays.asList(1L, 2L, 3L);

        when(commentService.findAuthorIdsForBan()).thenReturn(expectedAuthorIds);

        commenterBanner.runBannerTask();

        Object publishedMessage = messages.poll(5, TimeUnit.SECONDS);
        assertNotNull(publishedMessage, "The message was not published in Redis");

        List<?> publishedList = (List<?>) publishedMessage;
        assertEquals(expectedAuthorIds.size(), publishedList.size(), "List sizes differ");

        IntStream.range(0, expectedAuthorIds.size()).forEach(i -> {
            Number publishedNumber = (Number) publishedList.get(i);
            assertEquals(expectedAuthorIds.get(i).longValue(), publishedNumber.longValue(),
                    "Element at index " + i + " does not match");
        });
    }
}

package faang.school.postservice.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.util.BaseContextTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Интеграционные тесты RedisConfig")
@Tag("redis")
public class RedisConfigIntegrationTest extends BaseContextTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> genericRedisTemplate;

    @Autowired
    private ChannelTopic commentTopic;

    @Autowired
    private RedisMessageListenerContainer listenerContainer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Должен отправлять и получать сообщение через commentTopic")
    @Tag("redis")
    void shouldPublishAndReceiveMessageThroughCommentTopic() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> receivedMessage = new AtomicReference<>();

        MessageListener listener = (Message message, byte[] pattern) -> {
            receivedMessage.set(new String(message.getBody()));
            latch.countDown();
        };

        listenerContainer.addMessageListener(listener, commentTopic);

        String expected = "test-comment-event";

        stringRedisTemplate.convertAndSend(commentTopic.getTopic(), expected);

        boolean received = latch.await(2, TimeUnit.SECONDS);

        assertThat(received).isTrue();
        assertThat(receivedMessage.get()).isEqualTo(expected);

        listenerContainer.removeMessageListener(listener);
    }

    @Test
    @DisplayName("Должен работать с универсальными объектами через genericRedisTemplate")
    @Tag("redis")
    void genericRedisTemplate_shouldWorkWithDifferentObjects() {
        genericRedisTemplate.opsForValue().set("num", 42);

        Object value = genericRedisTemplate.opsForValue().get("num");

        assertThat(value).isEqualTo(42);
    }

    @Test
    @DisplayName("commentTopic должен возвращать корректное имя топика")
    @Tag("redis")
    void commentTopic_shouldReturnCorrectTopicName() {
        assertThat(commentTopic.getTopic()).isEqualTo("comment.event.topic");
    }
}
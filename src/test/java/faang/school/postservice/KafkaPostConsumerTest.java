package faang.school.postservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.consumer.KafkaPostConsumer;
import faang.school.postservice.dto.post.PostPublishDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaPostConsumerTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private PostService postService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private KafkaPostConsumer kafkaPostConsumer;

    private PostPublishDto postPublishDto;
    private Post post;

    @BeforeEach
    void setUp() throws Exception {
        postPublishDto = new PostPublishDto();
        postPublishDto.setPostId(1L);
        postPublishDto.setSubscribersIds(List.of(100L, 101L));

        post = new Post();
        post.setId(1L);
        post.setPublished(true);

        when(objectMapper.readValue(anyString(), eq(PostPublishDto.class))).thenReturn(postPublishDto);

        var field = KafkaPostConsumer.class.getDeclaredField("maxFeedSize");
        field.setAccessible(true);
        field.set(kafkaPostConsumer, 20);
    }

    @Test
    @DisplayName("Успешная обработка события публикации поста")
    void givenValidMessage_WhenConsumePostEvent_ThenSuccessfullyProcessed() throws Exception {
        String message = "{\"postId\":1,\"subscribersIds\":[100,101]}";
        when(postService.getPostEntity(1L)).thenReturn(post);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(valueOperations.increment("feed:counter")).thenReturn(1L);
        when(zSetOperations.zCard(anyString())).thenReturn(10L);
        doAnswer(invocation -> {
            RedisCallback<?> callback = invocation.getArgument(0);
            callback.doInRedis(null);
            return null;
        }).when(redisTemplate).executePipelined(any(RedisCallback.class));

        kafkaPostConsumer.consumePostEvent(message, acknowledgment);

        verify(objectMapper).readValue(message, PostPublishDto.class);
        verify(postService).getPostEntity(1L);
        verify(redisTemplate.opsForValue()).increment("feed:counter");
        verify(zSetOperations, times(2)).add(anyString(), eq("1"), eq(1.0));
        verify(zSetOperations, times(2)).zCard(anyString());
        verify(acknowledgment).acknowledge();
    }

    @Test
    @DisplayName("Обработка события с несуществующим постом")
    void givenMessageWithNonExistentPost_WhenConsumePostEvent_ThenNoFeedUpdate() throws Exception {
        String message = "{\"postId\":1,\"subscribersIds\":[100,101]}";
        when(postService.getPostEntity(1L)).thenReturn(null);

        kafkaPostConsumer.consumePostEvent(message, acknowledgment);

        verify(objectMapper).readValue(message, PostPublishDto.class);
        verify(postService).getPostEntity(1L);
        verify(redisTemplate, never()).opsForValue();
        verify(acknowledgment).acknowledge();
    }

    @Test
    @DisplayName("Обработка события с неопубликованным постом")
    void givenMessageWithUnpublishedPost_WhenConsumePostEvent_ThenNoFeedUpdate() throws Exception {
        String message = "{\"postId\":1,\"subscribersIds\":[100,101]}";
        post.setPublished(false);
        when(postService.getPostEntity(1L)).thenReturn(post);

        kafkaPostConsumer.consumePostEvent(message, acknowledgment);

        verify(objectMapper).readValue(message, PostPublishDto.class);
        verify(postService).getPostEntity(1L);
        verify(redisTemplate, never()).opsForValue();
        verify(acknowledgment).acknowledge();
    }

    @Test
    @DisplayName("Обработка события с ошибкой десериализации")
    void givenInvalidJson_WhenConsumePostEvent_ThenDeserializationError() throws Exception {
        String message = "invalid_json";
        when(objectMapper.readValue(message, PostPublishDto.class))
                .thenThrow(new RuntimeException("Deserialization error"));

        try {
            kafkaPostConsumer.consumePostEvent(message, acknowledgment);
        } catch (RuntimeException e) {
            verify(objectMapper).readValue(message, PostPublishDto.class);
            verify(postService, never()).getPostEntity(anyLong());
            verify(redisTemplate, never()).opsForValue();
            verify(acknowledgment, never()).acknowledge();
        }
    }

    @Test
    @DisplayName("Обработка события с неудачным инкрементом счетчика")
    void givenValidMessage_WhenProcessSubscribersFeed_ThenCounterIncrementFailed() {
        String message = "{\"postId\":1,\"subscribersIds\":[100,101]}";
        when(postService.getPostEntity(1L)).thenReturn(post);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment("feed:counter")).thenReturn(null);

        kafkaPostConsumer.consumePostEvent(message, acknowledgment);

        verify(redisTemplate.opsForValue()).increment("feed:counter");
        verify(redisTemplate, never()).opsForZSet();
        verify(acknowledgment).acknowledge();
    }
}
package faang.school.postservice.messaging.publisher;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.messaging.publisher.comment.CommentEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentEventPublisherTest {

  private static final String SERIALIZED_STRING = "serialized string";

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @Mock
  private ChannelTopic channelTopic;

  @InjectMocks
  private CommentEventPublisher eventPublisher;

  private CommentEvent commentEvent;

  @BeforeEach
  void setUp() {
    commentEvent = CommentEvent.builder()
        .postId(1L)
        .commentId(2L)
        .build();
  }

  @Test
  @DisplayName("Проверка публикации топика в Redis для комментария")
  void testPublicationTopicInRedisForComments() throws JsonProcessingException {
    when(objectMapper.writeValueAsString(commentEvent)).thenReturn(SERIALIZED_STRING);
    when(channelTopic.getTopic()).thenReturn("comment_channel");

    eventPublisher.publish(commentEvent);

    verify(objectMapper).writeValueAsString(commentEvent);
    verify(redisTemplate).convertAndSend(channelTopic.getTopic(), SERIALIZED_STRING);
  }

  @Test
  @DisplayName("Проверка выброса исключения при сериализации event-а")
  void publishThrowsEventPublishingException() throws JsonProcessingException {
    when(channelTopic.getTopic()).thenReturn("topic");
    when(objectMapper.writeValueAsString(commentEvent)).thenReturn("message");
    when(redisTemplate.convertAndSend(anyString(), anyString()))
        .thenThrow(new IllegalArgumentException("exception") {});

    verify(objectMapper, never()).writeValueAsString(commentEvent);
    verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
  }
}
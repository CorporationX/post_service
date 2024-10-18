package faang.school.postservice.publis.publisher;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.like.PostLikeEventDto;
import faang.school.postservice.mapper.like.LikeEventMapper;
import faang.school.postservice.model.Like;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class PostLikeEventPublisherTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private RedisProperties redisProperties;

    @Mock
    private LikeEventMapper likeEventMapper;

    @InjectMocks
    private LikeEventPublisher likeEventPublisher;

    @Test
    void shouldPublishPostLikeEventToBrokerSuccessfully() throws JsonProcessingException {
        Like like = mock(Like.class);
        PostLikeEventDto likeEvent = mock(PostLikeEventDto.class);
        when(likeEventMapper.toPostLikeEvent(like)).thenReturn(likeEvent);

        String channelName = "testChannel";
        String expectedJson = "{\"event\":\"like\"}";

        when(redisProperties.getPostLikeEventChannelName()).thenReturn(channelName);
        when(objectMapper.writeValueAsString(likeEvent)).thenReturn(expectedJson);

        likeEventPublisher.publishPostLikeEventToBroker(like);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(redisTemplate).convertAndSend(eq(channelName), captor.capture());
        assertEquals(expectedJson, captor.getValue());

        verifyNoMoreInteractions(objectMapper, redisTemplate, redisProperties);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenJsonProcessingFails() throws JsonProcessingException {
        Like like = mock(Like.class);
        PostLikeEventDto likeEvent = mock(PostLikeEventDto.class);
        String channelName = "testChannel";
        when(likeEventMapper.toPostLikeEvent(like)).thenReturn(likeEvent);

        when(redisProperties.getPostLikeEventChannelName()).thenReturn(channelName);
        when(objectMapper.writeValueAsString(likeEvent)).thenThrow(new JsonProcessingException("error") {
        });

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            likeEventPublisher.publishPostLikeEventToBroker(like);
        });
        assertEquals("error", exception.getCause().getMessage());
    }
}

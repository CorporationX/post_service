package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.publisher.NewPostPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NewPostPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic topic;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NewPostPublisher newPostPublisher;

    private PostDto postDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        postDto = PostDto.builder()
                .id(1L)
                .content("Test Content")
                .build();
    }

    @Test
    void shouldPublishMessageSuccessfully() throws JsonProcessingException {
        String jsonMessage = "{\"id\":1,\"title\":\"Test Title\",\"content\":\"Test Content\"}";
        when(objectMapper.writeValueAsString(postDto)).thenReturn(jsonMessage);

        when(topic.getTopic()).thenReturn("testTopic");

        newPostPublisher.publish(postDto);

        verify(redisTemplate, times(1)).convertAndSend("testTopic", jsonMessage);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenJsonProcessingFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(postDto)).thenThrow(new JsonProcessingException("Error") {
        });

        assertThrows(RuntimeException.class, () -> newPostPublisher.publish(postDto));

        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }
}
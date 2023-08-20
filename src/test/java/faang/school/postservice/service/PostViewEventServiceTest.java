package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.PostViewEventDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.redis.PostViewEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.listener.ChannelTopic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostViewEventServiceTest {
    public static final String TEST_TOPIC_NAME = "test-topic";

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private PostViewEventPublisher postViewEventPublisher;
    @Mock
    private ChannelTopic postViewTopic;
    @InjectMocks
    private PostViewEventService postViewEventService;

    @Test
    public void testGetPostViewEventDto() {
        Long userId = 1L;
        Post post = Post.builder().id(123L).authorId(11L).build();
        PostViewEventDto postViewEventDto = PostViewEventDto.builder().postId(post.getId()).authorId(post.getAuthorId()).build();
        postViewEventDto.setCreatedAt(LocalDateTime.now());
        postViewEventDto.setUserId(userId);

        objectMapper.convertValue(post, PostViewEventDto.class);

        assertNotNull(postViewEventDto);
        assertEquals(userId, postViewEventDto.getUserId());
        assertEquals(post.getId(), postViewEventDto.getPostId());
        assertNotNull(postViewEventDto.getCreatedAt());
    }

    @Test
    void testPublishEventToChannelSuccess() throws JsonProcessingException {
        Long userId = 123L;
        Post post = Post.builder().id(123L).authorId(1L).build();
        PostViewEventDto postViewEventDto = PostViewEventDto.builder().postId(post.getId()).authorId(post.getAuthorId()).build();
        postViewEventDto.setCreatedAt(LocalDateTime.now());
        postViewEventDto.setUserId(userId);
        String message = "{\"userId\":2,\"postId\":3,\"authorId\":null,\"projectId\":1,\"createdAt\":\"2023-08-20T21:38:20.3147821\"}";

        when(postViewTopic.getTopic()).thenReturn(TEST_TOPIC_NAME);
        when(objectMapper.writeValueAsString(postViewEventDto)).thenReturn(message);

        postViewEventService.publishEventToChannel(postViewEventDto);

        verify(postViewEventPublisher).publish(TEST_TOPIC_NAME, message);
    }
}

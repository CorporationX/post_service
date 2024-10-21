package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostCreatedEvent;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCreatedEventPublisherTest {
    private static final String POST_CREATED_TOPIC = "post_сreated_topic";

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PostCreatedEventPublisher postCreatedEventPublisher;

    private Post post;
    private PostCreatedEvent postCreatedEvent;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postCreatedEventPublisher, "postCreatedTopic", POST_CREATED_TOPIC);

        post = Post.builder()
                .id(1L)
                .authorId(2L)
                .build();
        postCreatedEvent = PostCreatedEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .build();
    }

    @Test
    void testPublishPostEvent_Success() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any(PostCreatedEvent.class))).thenReturn(postCreatedEvent.toString());

        postCreatedEventPublisher.publishPostEvent(post);

        verify(redisTemplate).convertAndSend(anyString(), anyString());
    }
}
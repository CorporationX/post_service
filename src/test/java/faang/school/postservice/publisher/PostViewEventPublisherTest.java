package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.annotations.SendPostViewEventToAnalytics;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostViewEventPublisherTest {

    private final long actorId = 1L;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private UserContext userContext;

    @Mock
    private ObjectMapper javaTimeModuleObjectMapper;

    @Mock
    private SendPostViewEventToAnalytics sendPostViewEventToAnalytics;

    @InjectMocks
    private PostViewEventPublisher postViewEventPublisher;

    @Value("${spring.data.redis.channels.post-view}")
    private String postViewChannel;
    private Post post;

    @BeforeEach
    void setUp() {
        post = buildPost(1L, 2L);
    }

    @Test
    void testPublishPostEvent_SinglePost() throws JsonProcessingException {
        when(userContext.getUserId()).thenReturn(actorId);
        when(sendPostViewEventToAnalytics.value()).thenReturn((Class) Post.class);
        PostViewEvent event = postViewEventPublisher.createEvent(post, actorId);
        when(javaTimeModuleObjectMapper.writeValueAsString(any(PostViewEvent.class))).thenReturn(event.toString());

        postViewEventPublisher.publishPostEvent(post, sendPostViewEventToAnalytics);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(redisTemplate).convertAndSend(topicCaptor.capture(), messageCaptor.capture());
        assertEquals(postViewChannel, topicCaptor.getValue());
        assertEquals(event.toString(), messageCaptor.getValue());
    }

    @Test
    void testPublishPostEvent_ListOfPosts() {
        Post post2 = buildPost(2L, 2L);
        List<Post> posts = List.of(post, post2);
        when(sendPostViewEventToAnalytics.value()).thenReturn((Class) List.class);

        postViewEventPublisher.publishPostEvent(posts, sendPostViewEventToAnalytics);

        verify(redisTemplate, times(2)).convertAndSend(eq(postViewChannel), any());
    }

    @Test
    void testCreatePostViewEvent() {
        PostViewEvent expected = new PostViewEvent(post.getId(), post.getAuthorId(), actorId, LocalDateTime.now());

        PostViewEvent postViewEvent = postViewEventPublisher.createEvent(post, actorId);

        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(postViewEvent);
    }

    private Post buildPost(long id, long authorId) {
        return Post.builder()
                .id(id)
                .authorId(authorId)
                .build();
    }
}
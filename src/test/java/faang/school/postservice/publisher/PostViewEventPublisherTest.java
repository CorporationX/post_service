package faang.school.postservice.publisher;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Assertions;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostViewEventPublisherTest {

    private final long actorId = 1L;

    @Mock
    private RedisTemplate<String, PostViewEvent> eventRedisTemplate;

    @Mock
    private UserContext userContext;

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
    void testPublishPostEvent_SinglePost() {
        when(userContext.getUserId()).thenReturn(actorId);

        postViewEventPublisher.publishPostEvent(post);

        ArgumentCaptor<PostViewEvent> eventCaptor = ArgumentCaptor.forClass(PostViewEvent.class);
        verify(eventRedisTemplate).convertAndSend(eq(postViewChannel), eventCaptor.capture());

        PostViewEvent capturedEvent = eventCaptor.getValue();

        Assertions.assertEquals(post.getId(), capturedEvent.getPostId());
        Assertions.assertEquals(post.getAuthorId(), capturedEvent.getReceiverId());
        Assertions.assertEquals(actorId, capturedEvent.getActorId());
    }

    @Test
    void testPublishPostEvent_ListOfPosts() {
        Post post2 = buildPost(2L, 2L);
        List<Post> posts = List.of(post, post2);

        postViewEventPublisher.publishPostEvent(posts);

        verify(eventRedisTemplate, times(2)).convertAndSend(eq(postViewChannel), any(PostViewEvent.class));
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
package faang.school.postservice.service.posts;

import faang.school.postservice.dto.post.PostToFeedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class FeedServiceImplTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOps;

    @InjectMocks
    private FeedServiceImpl feedService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
        feedService = new FeedServiceImpl(redisTemplate);

        Field maxFeedSizeField = FeedServiceImpl.class.getDeclaredField("maxFeedSize");
        maxFeedSizeField.setAccessible(true);
        maxFeedSizeField.set(feedService, 500);
    }

    @Test
    void addPostToFeeds_shouldAddPostsToSubscribers() {
        PostToFeedEvent event = PostToFeedEvent.builder()
                .postId(123L)
                .authorId(1L)
                .subscriberIds(List.of(10L, 20L))
                .createdAt(1000L)
                .build();

        feedService.addPostToFeeds(event);

        verify(zSetOps, times(2)).add(anyString(), eq(123L), eq(1000.0));

        verify(zSetOps, times(2)).removeRange(anyString(), eq(0L), eq(-501L));
    }

    @Test
    void addPostToFeed_shouldUseCorrectKey() {
        feedService.addPostToFeeds(PostToFeedEvent.builder()
                .postId(1L)
                .authorId(1L)
                .subscriberIds(List.of(42L))
                .createdAt(555L)
                .build());

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(zSetOps).add(keyCaptor.capture(), eq(1L), eq(555.0));
        assertEquals("feed:42", keyCaptor.getValue());
    }
}
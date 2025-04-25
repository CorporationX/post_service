package faang.school.postservice.service.hashtags;

import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HashtagRedisServiceTest {
    @InjectMocks
    private HashtagRedisService hashtagRedisService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOps;

    @Mock
    private HashOperations<String, String, String> hashOps;

    private static final String HASHTAG_KEY = "post#";
    private static final String POST_ID_KEY = "postId:";
    private static final String CONTENT_HASH_KEY = "content:";
    private static final String AUTHOR_HASH_KEY = "author:";
    private static final String PUBLISHED_DATE_HASH_KEY = "publishedAt:";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private static final Duration ONE_DAY_TTL = Duration.ofDays(1);

    private final int maxCachedPosts = 100;
    private final String tag = "tag1";
    private Post post;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(hashtagRedisService, "maxCachedPosts", maxCachedPosts);
        post = Post.builder().id(1L).content("content").authorId(1L).publishedAt(LocalDateTime.now()).build();
    }

    @Test
    public void testSaveHashtag_noEviction() {
        String postIdStr = String.valueOf(post.getId());
        String postIdKey = POST_ID_KEY + post.getId();
        String hashtagKey = HASHTAG_KEY + tag;
        when(zSetOps.size(hashtagKey)).thenReturn((long) maxCachedPosts - 1);

        hashtagRedisService.saveHashtag(tag, post);

        verify(hashOps, times(1))
                .put(postIdKey, CONTENT_HASH_KEY, post.getContent());
        verify(hashOps, times(1))
                .put(postIdKey, AUTHOR_HASH_KEY, String.valueOf(post.getAuthorId()));
        verify(hashOps, times(1))
                .put(postIdKey, PUBLISHED_DATE_HASH_KEY, post.getPublishedAt().format(dateFormatter));
        verify(zSetOps, times(1))
                .add(hashtagKey, postIdStr, post.getPublishedAt()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toEpochSecond());

        verify(redisTemplate, times(1)).expire(hashtagKey, ONE_DAY_TTL);
        verify(redisTemplate, times(1)).expire(postIdKey, ONE_DAY_TTL);
    }

    @Test
    public void testSaveHashtag_withEviction() {
        String postIdStr = String.valueOf(post.getId());
        String postIdKey = POST_ID_KEY + post.getId();
        String hashtagKey = HASHTAG_KEY + tag;
        when(zSetOps.size(hashtagKey)).thenReturn((long) maxCachedPosts + 1);
        Set<String> removedPostsSet = new HashSet<>();
        removedPostsSet.add(postIdStr);
        when(zSetOps.range(hashtagKey, 0, 0)).thenReturn(removedPostsSet);

        hashtagRedisService.saveHashtag(tag, post);

        verify(hashOps, times(1))
                .put(postIdKey, CONTENT_HASH_KEY, post.getContent());
        verify(hashOps, times(1))
                .put(postIdKey, AUTHOR_HASH_KEY, String.valueOf(post.getAuthorId()));
        verify(hashOps, times(1))
                .put(postIdKey, PUBLISHED_DATE_HASH_KEY, post.getPublishedAt().format(dateFormatter));
        verify(zSetOps, times(1))
                .add(hashtagKey, postIdStr, post.getPublishedAt()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toEpochSecond());
        verify(zSetOps, times(1)).remove(hashtagKey, postIdStr);

        verify(redisTemplate, times(1)).expire(hashtagKey, ONE_DAY_TTL);
        verify(redisTemplate, times(1)).expire(postIdKey, ONE_DAY_TTL);
    }
}

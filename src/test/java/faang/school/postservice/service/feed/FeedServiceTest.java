package faang.school.postservice.service.feed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.cache.RedisCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    @InjectMocks
    private FeedService feedService;

    @Mock
    private ZSetOperations<String, String> zSetOperations;
    @Mock
    private UserContext userContext;
    @Mock
    private RedisCacheService redisCacheService;


    @Value("${spring.data.redis.directory.feed}")
    private String feed = "feed:";
    @Value("${spring.data.redis.directory.post}")
    private String post = "post:";
    @Value("${spring.data.redis.directory.comment}")
    private String comment = "comment:";
    @Value("${spring.data.redis.directory.infAuthor}")
    private String infAuthor = "infAuthor:";
    @Value("${value.feedSize}")
    private int feedSize = 10;

    @BeforeEach
    public void setUp() {
        feedService.setFeed(feed);
        feedService.setPost(post);
        feedService.setComment(comment);
        feedService.setInfAuthor(infAuthor);
        feedService.setFeedSize(feedSize);
    }

    @Test
    public void testGetFeedWhenNoPosts() {
        long userId = 1L;
        String key = feed + userId;

        when(userContext.getUserId()).thenReturn(userId);
        when(zSetOperations.reverseRangeByScore(key, Double.MIN_VALUE, Double.MAX_VALUE, 0, feedSize)).thenReturn(Collections.emptySet());

        List<FeedDto> result = feedService.getFeed(null);

        assertNull(result);
        verify(userContext, times(1)).getUserId();
        verify(zSetOperations, times(1)).reverseRangeByScore(key, Double.MIN_VALUE, Double.MAX_VALUE, 0, feedSize);
    }

    @Test
    public void testGetFeedWithPosts() {
        long userId = 1L;
        String afterPostId = "post1";
        String key = feed + userId;
        Double score = 50.0;

        String postId = "post1";
        String postInfo = "{\"authorId\": 2}";
        Long authorId = 2L;
        String authorInfo = "authorInfo";
        Set<String> commentInfo = Set.of("comment1", "comment2");
        Long likeInfo = 10L;

        when(userContext.getUserId()).thenReturn(userId);
        when(zSetOperations.score(key, afterPostId)).thenReturn(score);
        when(zSetOperations.reverseRangeByScore(key, Double.MIN_VALUE, score - 1, 0, feedSize)).thenReturn(Set.of(postId));
        when(redisCacheService.getFromHSetCache(post, postId)).thenReturn(postInfo);
        when(redisCacheService.getFromHSetCache(infAuthor, authorId.toString())).thenReturn(authorInfo);
        when(redisCacheService.getAllZSetValues(comment + postId)).thenReturn(commentInfo);
        when(redisCacheService.getZSetSize(comment + postId)).thenReturn(likeInfo);

        List<FeedDto> result = feedService.getFeed(afterPostId);

        assertNotNull(result);
        assertEquals(1, result.size());

        FeedDto feedDto = result.get(0);
        assertEquals(postInfo, feedDto.getPostInfo());
        assertEquals(authorInfo, feedDto.getAuthorInfo());
        assertEquals(commentInfo, feedDto.getCommentInfo());
        assertEquals(likeInfo, feedDto.getLikeInfo());

        verify(userContext, times(1)).getUserId();
        verify(zSetOperations, times(1)).score(key, afterPostId);
        verify(zSetOperations, times(1)).reverseRangeByScore(key, Double.MIN_VALUE, score - 1, 0, feedSize);
        verify(redisCacheService, times(1)).getFromHSetCache(post, postId);
        verify(redisCacheService, times(1)).getFromHSetCache(infAuthor, authorId.toString());
        verify(redisCacheService, times(1)).getAllZSetValues(comment + postId);
        verify(redisCacheService, times(1)).getZSetSize(comment + postId);
    }

    @Test
    public void testExtractAuthorIdFromJson() throws Exception {
        String json = "{\"authorId\": 123}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        Long authorId = feedService.extractAuthorIdFromJson(json);

        assertNotNull(authorId);
        assertEquals(123L, authorId);
    }

    @Test
    public void testExtractAuthorIdFromJsonWithNoAuthorId() {
        String json = "{\"otherField\": 123}";

        Long authorId = feedService.extractAuthorIdFromJson(json);

        assertNull(authorId);
    }

    @Test
    public void testExtractAuthorIdFromJsonWithInvalidJson() {
        String invalidJson = "{";

        Long authorId = feedService.extractAuthorIdFromJson(invalidJson);

        assertNull(authorId);
    }
}
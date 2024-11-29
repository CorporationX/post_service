package faang.school.postservice.service.redis;

import faang.school.postservice.dto.comment.CommentNewsFeedDto;
import faang.school.postservice.mapper.redis.CachedPostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CachedPost;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.CachedPostRepository;
import faang.school.postservice.service.post.PostQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CachedPostServiceTest {
    @Mock
    private RedisTemplate<String, CachedPost> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ValueOperations<String, CachedPost> valueOperations;

    @Mock
    private CachedPostRepository cachedPostRepository;

    @Mock
    private CachedPostMapper cachedPostMapper;

    @Mock
    private PostQueryService postQueryService;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CachedPostService cachedPostService;

    @Captor
    private ArgumentCaptor<CachedPost> cachedPostCaptor;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(cachedPostService, "postCacheViewsField", "views");
        ReflectionTestUtils.setField(cachedPostService, "postCacheLikesField", "likes");
        ReflectionTestUtils.setField(cachedPostService, "postCacheKeyPrefix", "post:");
        ReflectionTestUtils.setField(cachedPostService, "maxCommentsQuantity", 3);
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testAddPostView_Success() {
        Long postId = 1L;
        long viewCount = 5L;
        String cacheKey = "post:" + postId;

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.increment(cacheKey, "views", viewCount)).thenReturn(viewCount);

        cachedPostService.addPostView(postId, viewCount);

        verify(hashOperations).increment(cacheKey, "views", viewCount);
    }

//    @Test
//    public void testAddPostView_FallbackToDB() {
//        Long postId = 1L;
//        long viewCount = 5L;
//        String cacheKey = "post:" + postId;
//
//        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
//        doThrow(new RuntimeException("Redis error"))
//                .when(hashOperations)
//                .increment(cacheKey, "views", viewCount);
//
//        Post post = Post.builder()
//                .id(postId)
//                .views(10L)
//                .build();
//
//        CachedPost cachedPost = CachedPost.builder()
//                .id(postId)
//                .views(10L)
//                .build();
//
//        when(postQueryService.findPostById(postId)).thenReturn(post);
//        when(cachedPostMapper.toCachedPost(post)).thenReturn(cachedPost);
//
//        assertDoesNotThrow(() -> cachedPostService.addPostView(postId, viewCount));
//
//        verify(cachedPostRepository).save(cachedPostCaptor.capture());
//        CachedPost savedCachedPost = cachedPostCaptor.getValue();
//
//        assertEquals(Long.valueOf(15L), savedCachedPost.getViews());
//    }

    @Test
    public void testIncrementPostLikes_Success() {
        Long postId = 1L;
        String cacheKey = "post:" + postId;

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.increment(cacheKey, "likes", 1L)).thenReturn(1L);

        cachedPostService.incrementPostLikes(postId);

        verify(hashOperations).increment(cacheKey, "likes", 1L);
    }

//    @Test
//    public void testIncrementPostLikes_FallbackToDB() {
//        Long postId = 1L;
//        String cacheKey = "post:" + postId;
//
//        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
//        doThrow(new RuntimeException("Redis error"))
//                .when(hashOperations)
//                .increment(cacheKey, "likes", 1L);
//
//        Post post = Post.builder()
//                .id(postId)
//                .likes(List.of(new Like(), new Like()))
//                .build();
//
//        CachedPost cachedPost = CachedPost.builder()
//                .id(postId)
//                .likes(2L)
//                .build();
//
//        when(postQueryService.findPostById(postId)).thenReturn(post);
//        when(cachedPostMapper.toCachedPost(post)).thenReturn(cachedPost);
//
//        cachedPostService.incrementPostLikes(postId);
//
//        verify(cachedPostRepository).save(cachedPostCaptor.capture());
//        CachedPost savedCachedPost = cachedPostCaptor.getValue();
//
//        assertEquals(Long.valueOf(3L), savedCachedPost.getLikes());
//    }

    @Test
    public void testDecrementPostLikes_Success() {
        Long postId = 1L;
        String cacheKey = "post:" + postId;

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.increment(cacheKey, "likes", -1L)).thenReturn(0L);

        cachedPostService.decrementPostLikes(postId);

        verify(hashOperations).increment(cacheKey, "likes", -1L);
    }

//    @Test
//    public void testDecrementPostLikes_FallbackToDB() {
//        Long postId = 1L;
//        String cacheKey = "post:" + postId;
//
//        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
//        doThrow(new RuntimeException("Redis error"))
//                .when(hashOperations)
//                .increment(cacheKey, "likes", -1L);
//
//        Post post = Post.builder()
//                .id(postId)
//                .likes(List.of(new Like(), new Like()))
//                .build();
//
//        CachedPost cachedPost = CachedPost.builder()
//                .id(postId)
//                .likes(2L)
//                .build();
//
//        when(postQueryService.findPostById(postId)).thenReturn(post);
//        when(cachedPostMapper.toCachedPost(post)).thenReturn(cachedPost);
//
//        cachedPostService.decrementPostLikes(postId);
//
//        verify(cachedPostRepository).save(cachedPostCaptor.capture());
//        CachedPost savedCachedPost = cachedPostCaptor.getValue();
//
//        assertEquals(Long.valueOf(1L), savedCachedPost.getLikes());
//    }

    @Test
    public void testAddCommentToCachedPost_CachedPostExists() {
        Long postId = 1L;
        CommentNewsFeedDto commentDto = CommentNewsFeedDto.builder()
                .id(100L)
                .build();

        CachedPost cachedPost = CachedPost.builder()
                .id(postId)
                .comments(new ConcurrentLinkedQueue<>())
                .build();

        when(cachedPostRepository.findById(postId)).thenReturn(Optional.of(cachedPost));

        cachedPostService.addCommentToCachedPost(postId, commentDto);

        verify(cachedPostRepository).save(cachedPostCaptor.capture());
        CachedPost savedCachedPost = cachedPostCaptor.getValue();

        assertEquals(1, savedCachedPost.getComments().size());
        assertTrue(savedCachedPost.getComments().contains(commentDto));
    }

    @Test
    public void testAddCommentToCachedPost_CachedPostDoesNotExist() {
        Long postId = 1L;
        CommentNewsFeedDto commentDto = CommentNewsFeedDto.builder()
                .id(100L)
                .build();

        Post post = Post.builder()
                .id(postId)
                .build();

        CachedPost cachedPost = CachedPost.builder()
                .id(postId)
                .comments(new ConcurrentLinkedQueue<>())
                .build();

        when(cachedPostRepository.findById(postId)).thenReturn(Optional.empty());
        when(postQueryService.findPostById(postId)).thenReturn(post);
        when(cachedPostMapper.toCachedPost(post)).thenReturn(cachedPost);

        cachedPostService.addCommentToCachedPost(postId, commentDto);

        verify(cachedPostRepository).save(cachedPostCaptor.capture());
        CachedPost savedCachedPost = cachedPostCaptor.getValue();

        assertEquals(1, savedCachedPost.getComments().size());
        assertTrue(savedCachedPost.getComments().contains(commentDto));
    }

    @Test
    public void testGetCachedPostByIds() {
        List<Long> postIds = Arrays.asList(1L, 2L);
        CachedPost cachedPost1 = CachedPost.builder().id(1L).build();
        CachedPost cachedPost2 = CachedPost.builder().id(2L).build();

        when(cachedPostRepository.findAllById(postIds))
                .thenReturn(Arrays.asList(cachedPost1, cachedPost2));

        List<CachedPost> result = cachedPostService.getCachedPostByIds(postIds);

        assertEquals(2, result.size());
        assertTrue(result.contains(cachedPost1));
        assertTrue(result.contains(cachedPost2));
    }

    @Test
    public void testSavePostCache() {
        Post post = Post.builder()
                .id(1L)
                .build();

        CachedPost cachedPost = CachedPost.builder()
                .id(1L)
                .build();

        when(cachedPostMapper.toCachedPost(post)).thenReturn(cachedPost);

        String cacheKey = "post:" + post.getId();

        cachedPostService.savePostCache(post);

        verify(valueOperations).set(cacheKey, cachedPost);
    }

    @Test
    public void testSyncViewsFromCacheToDB() {
        String cacheKey1 = "post:1";
        String cacheKey2 = "post:2";

        Set<String> cacheKeys = new HashSet<>(Arrays.asList(cacheKey1, cacheKey2));

        when(redisTemplate.keys("post:*")).thenReturn(cacheKeys);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        when(hashOperations.get(cacheKey1, "views")).thenReturn(5L);
        when(hashOperations.get(cacheKey2, "views")).thenReturn(10L);

        Post post1 = Post.builder()
                .id(1L)
                .views(100L)
                .build();

        Post post2 = Post.builder()
                .id(2L)
                .views(200L)
                .build();

        when(postQueryService.findPostById(1L)).thenReturn(post1);
        when(postQueryService.findPostById(2L)).thenReturn(post2);

        cachedPostService.syncViewsFromCacheToDB();

        verify(postRepository, times(2)).save(postCaptor.capture());

        List<Post> savedPosts = postCaptor.getAllValues();

        assertEquals(2, savedPosts.size());

        Post savedPost1 = savedPosts.stream().filter(p -> p.getId() == 1L).findFirst().orElse(null);
        Post savedPost2 = savedPosts.stream().filter(p -> p.getId() == 2L).findFirst().orElse(null);

        assertNotNull(savedPost1);
        assertNotNull(savedPost2);

        assertEquals(Long.valueOf(105L), savedPost1.getViews());
        assertEquals(Long.valueOf(210L), savedPost2.getViews());
    }
}
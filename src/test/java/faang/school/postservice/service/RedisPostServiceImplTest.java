package faang.school.postservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.RedisPostDtoMapper;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.impl.RedisPostServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisPostServiceImplTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private RedisPostDtoMapper redisPostDtoMapper;

    @Mock
    private PostMapper postMapper;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private RedisConnection redisConnection;

    @InjectMocks
    private RedisPostServiceImpl redisPostService;

    @Test
    void testGetPost_PostInRedis() {
        Long postId = 100L;
        String postKey = "post:" + postId;

        Map<Object, Object> redisData = new HashMap<>();
        redisData.put("postId", "100");
        redisData.put("authorId", "10");
        redisData.put("content", "Test content");
        redisData.put("createdAt", "2024-12-09T16:59:33");
        redisData.put("commentCount", "0");
        redisData.put("likeCount", "0");
        redisData.put("viewCount", "0");
        redisData.put("recentComments", "[]");


        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(postKey)).thenReturn(redisData);

        RedisPostDto result = redisPostService.getPost(postId);

        assertNotNull(result);
        assertEquals(100L, result.getPostId());
        assertEquals(10L, result.getAuthorId());
        assertEquals("Test content", result.getContent());
    }

    @Test
    void testGetPost_PostNotInRedis_FetchFromDB() {
        Long postId = 200L;
        String postKey = "post:" + postId;

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(postKey)).thenReturn(Collections.emptyMap());

        doAnswer(invocation -> {
            RedisCallback<?> callback = invocation.getArgument(0);
            return callback.doInRedis(redisConnection);
        }).when(redisTemplate).execute(any(RedisCallback.class));

        doNothing().when(redisConnection).multi();
        when(redisConnection.exec()).thenReturn(Collections.emptyList());

        Post post = new Post();
        post.setId(postId);
        post.setAuthorId(20L);
        post.setContent("DB content");
        post.setCreatedAt(LocalDateTime.now());

        PostDto postDto = new PostDto();
        postDto.setId(postId);
        postDto.setAuthorId(20L);
        postDto.setContent("DB content");
        postDto.setCreatedAt(LocalDateTime.now());

        RedisPostDto redisPostDto = new RedisPostDto();
        redisPostDto.setPostId(postId);
        redisPostDto.setAuthorId(20L);
        redisPostDto.setContent("DB content");
        redisPostDto.setCreatedAt(LocalDateTime.now());

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toPostDto(post)).thenReturn(postDto);
        when(redisPostDtoMapper.mapToRedisPostDto(postDto)).thenReturn(redisPostDto);

        RedisPostDto result = redisPostService.getPost(postId);

        assertNotNull(result);
        assertEquals(postId, result.getPostId());
        assertEquals(20L, result.getAuthorId());
        assertEquals("DB content", result.getContent());

        verify(postRepository).findById(postId);
        verify(hashOperations, atLeastOnce()).put(eq("post:200"), anyString(), any());
    }

    @Test
    void testIncrementLikesWithTransaction_NewLike() {
        Long postId = 700L;
        Long likeId = 2L;
        String likeKey = "like:" + likeId;
        String postKey = "post:" + postId;

        Post post = new Post();
        post.setId(postId);
        post.setAuthorId(70L);
        post.setContent("Like test");
        post.setCreatedAt(LocalDateTime.now());

        PostDto postDto = new PostDto();
        postDto.setId(postId);

        RedisPostDto redisPostDto = new RedisPostDto();
        redisPostDto.setPostId(postId);
        redisPostDto.setAuthorId(70L);
        redisPostDto.setCreatedAt(LocalDateTime.now());

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toPostDto(post)).thenReturn(postDto);
        when(redisPostDtoMapper.mapToRedisPostDto(postDto)).thenReturn(redisPostDto);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq(likeKey), eq("processed"), any())).thenReturn(true);
        when(hashOperations.entries(postKey)).thenReturn(Collections.emptyMap());

        doAnswer(invocation -> {
            RedisCallback<?> callback = invocation.getArgument(0);
            return callback.doInRedis(redisConnection);
        }).when(redisTemplate).execute(any(RedisCallback.class));

        doNothing().when(redisConnection).multi();
        when(redisConnection.exec()).thenReturn(Collections.emptyList());

        redisPostService.incrementLikesWithTransaction(postId, likeId);

        verify(hashOperations, atLeastOnce()).increment(eq(postKey), eq("likeCount"), eq(1L));
        verify(redisConnection, times(2)).multi();
        verify(redisConnection, times(2)).exec();
    }

    @Test
    void testSavePostIfNotExists_ExistingPost() {
        Long postId = 300L;
        RedisPostDto postDto = new RedisPostDto();
        postDto.setPostId(postId);

        String postKey = "post:" + postId;
        when(redisTemplate.hasKey(postKey)).thenReturn(true);

        redisPostService.savePostIfNotExists(postDto);

        verify(redisTemplate, never()).execute(any(RedisCallback.class));
    }

    @Test
    void testSavePostIfNotExists_NewPost() {
        Long postId = 400L;
        RedisPostDto postDto = new RedisPostDto();
        postDto.setPostId(postId);

        String postKey = "post:" + postId;
        when(redisTemplate.hasKey(postKey)).thenReturn(false);

        redisPostService.savePostIfNotExists(postDto);

        verify(redisTemplate).execute(any(RedisCallback.class));
    }

    @Test
    void testAddComment_NewComment() throws Exception {
        Long postId = 500L;
        Long commentId = 5L;
        Long authorId = 50L;
        String content = "Test comment";

        String commentKey = "comment:" + commentId;
        String postKey = "post:" + postId;

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq(commentKey), eq("processed"), any())).thenReturn(true);

        Map<Object, Object> postMap = new HashMap<>();
        postMap.put("postId", postId.toString());
        postMap.put("recentComments", "[]");
        postMap.put("commentCount", "0");
        when(hashOperations.entries(postKey)).thenReturn(postMap);

        doAnswer(invocation -> {
            RedisCallback<?> callback = invocation.getArgument(0);
            return callback.doInRedis(redisConnection);
        }).when(redisTemplate).execute(any(RedisCallback.class));
        doNothing().when(redisConnection).multi();
        when(redisConnection.exec()).thenReturn(Collections.emptyList());

        when(objectMapper.readValue(eq("[]"), any(TypeReference.class))).thenReturn(new ArrayList<>());
        when(objectMapper.writeValueAsString(anyList()))
                .thenReturn("[{\"authorId\":50,\"content\":\"Test comment\"}]");

        redisPostService.addComment(postId, commentId, authorId, content);

        verify(hashOperations).put(eq(postKey), eq("recentComments"), anyString());
        verify(hashOperations).put(eq(postKey), eq("commentCount"), eq("1"));
        verify(redisConnection).multi();
        verify(redisConnection).exec();
    }


    @Test
    void testIncrementPostViewsWithTransaction_NewView() {
        Long postId = 600L;
        Long viewerId = 60L;
        String viewDateTime = "2024-12-10T12:00:00";
        String postViewKey = "postView:post:" + postId + ":user:" + viewerId + ":viewDateTime:" + viewDateTime;
        String postKey = "post:" + postId;

        // Настройка моков Redis
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq(postViewKey), eq("processed"), any())).thenReturn(true);

        Map<Object, Object> postMap = new HashMap<>();
        postMap.put("postId", postId.toString());
        postMap.put("viewCount", "0");
        when(hashOperations.entries(postKey)).thenReturn(postMap);

        doAnswer(invocation -> {
            RedisCallback<?> callback = invocation.getArgument(0);
            return callback.doInRedis(redisConnection);
        }).when(redisTemplate).execute(any(RedisCallback.class));
        doNothing().when(redisConnection).multi();
        when(redisConnection.exec()).thenReturn(Collections.emptyList());

        redisPostService.incrementPostViewsWithTransaction(postId, viewerId, viewDateTime);

        verify(hashOperations).increment(eq(postKey), eq("viewCount"), eq(1L));
        verify(redisConnection).multi();
        verify(redisConnection).exec();
    }


    @Test
    void testSavePosts_BatchSave() {
        RedisPostDto post1 = new RedisPostDto();
        post1.setPostId(700L);
        RedisPostDto post2 = new RedisPostDto();
        post2.setPostId(800L);

        List<RedisPostDto> posts = List.of(post1, post2);

        redisPostService.savePosts(posts);

        verify(redisTemplate).executePipelined(any(RedisCallback.class));
    }

}




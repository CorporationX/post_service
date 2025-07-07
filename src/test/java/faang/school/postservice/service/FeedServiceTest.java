package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostFeedResponseDto;
import faang.school.postservice.entity.CachedPost;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private UserContext context;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostCacheService postCacheService;

    @Mock
    private ZSetOperations<String, String> zSetOps;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    private FeedService feedService;

    private CachedPost cachedPost;
    private PostFeedResponseDto responseDto;

    @BeforeEach
    void setup() {
        when(stringRedisTemplate.opsForZSet()).thenReturn(zSetOps);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(context.getUserId()).thenReturn(100L);

        feedService = new FeedService(
                context,
                stringRedisTemplate,
                redisTemplate,
                userRepository,
                postRepository,
                postMapper,
                postCacheService
        );

        setField(feedService, "limit", 10L);
        setField(feedService, "feedSize", 50L);

        cachedPost = CachedPost.builder()
                .id(1L)
                .authorId(2L)
                .content("test")
                .likes(1)
                .publishedAt(Instant.now())
                .build();

        responseDto = PostFeedResponseDto.builder().id(1L).content("test").build();
    }

    @Test
    void positiveGetFeedIfCacheExists() {
        when(zSetOps.range("user:feed:100", 0, 9)).thenReturn(Set.of("1"));
        when(hashOperations.get("posts:", "1")).thenReturn(cachedPost);
        when(postMapper.toPostFeedResponseDto(cachedPost)).thenReturn(responseDto);

        List<PostFeedResponseDto> result = feedService.getFeed(null);

        assertThat(result).hasSize(1).containsExactly(responseDto);
    }

    @Test
    void positiveGetFeedFillFromDb() {
        when(zSetOps.rank("user:feed:100", "5")).thenReturn(null);
        when(zSetOps.range("user:feed:100", 0, 9)).thenReturn(Set.of("1"));
        when(hashOperations.get("posts:", "1")).thenReturn(cachedPost);
        when(postMapper.toPostFeedResponseDto(cachedPost)).thenReturn(responseDto);

        List<PostFeedResponseDto> result = feedService.getFeed(5L);

        assertThat(result).hasSize(1).containsExactly(responseDto);
        verify(userRepository).findFolloweesIdsByUserId(100L);
    }

    @Test
    void positiveGetFeedWhenPostOutFromCacheAndLoadFromDb() {
        when(zSetOps.range("user:feed:100", 0, 9)).thenReturn(Set.of("1"));
        when(hashOperations.get("posts:", "1")).thenReturn(null);
        when(postRepository.findById(1L)).thenReturn(Optional.of(new Post()));
        when(postMapper.toCachedPost(any())).thenReturn(cachedPost);
        when(postMapper.toPostFeedResponseDto(cachedPost)).thenReturn(responseDto);

        List<PostFeedResponseDto> result = feedService.getFeed(null);

        assertThat(result).hasSize(1).containsExactly(responseDto);
        verify(postCacheService).cachePost(cachedPost);
    }

    @Test
    void positiveGetFeedFilteredIfPostNotFound() {
        when(zSetOps.range("user:feed:100", 0, 9)).thenReturn(Set.of("1"));
        when(hashOperations.get("posts:", "1")).thenReturn(null);
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        List<PostFeedResponseDto> result = feedService.getFeed(null);

        assertThat(result).isEmpty();
    }
}

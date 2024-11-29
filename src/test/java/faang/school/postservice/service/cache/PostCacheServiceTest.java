package faang.school.postservice.service.cache;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.repository.cache.CacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCacheServiceTest {

    @Mock
    private CacheRepository<PostDto> cacheRepository;

    @InjectMocks
    private PostCacheService postCacheService;

    private int timeToLivePost;
    private Long postId;
    private String postKey;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        timeToLivePost = 5;
        postId = 123L;
        postKey = postId + "::post";
        
        ReflectionTestUtils.setField(postCacheService, "timeToLivePost", timeToLivePost);
        postDto = new PostDto();
    }

    @Test
    void save() {
        Duration expectedDuration = Duration.ofHours(timeToLivePost);

        postCacheService.save(postId, postDto);

        verify(cacheRepository).set(postKey, postDto, expectedDuration);
    }

    @Test
    void get_success() {
        when(cacheRepository.get(postKey, PostDto.class)).thenReturn(Optional.of(postDto));
        
        PostDto result = postCacheService.get(postId);
        
        assertNotNull(result);
        assertEquals(postDto, result);
    }

    @Test
    void get_notFound() {
        when(cacheRepository.get(postKey, PostDto.class)).thenReturn(Optional.empty());

        PostDto result = postCacheService.get(postId);

        assertNull(result);
    }

    @Test
    void getAll_success() {
        when(cacheRepository.get(postKey, PostDto.class)).thenReturn(Optional.of(postDto));
        
        List<PostDto> result = postCacheService.getAll(postId);

        assertNotNull(result);
        assertEquals(postDto, result.get(0));
    }

    @Test
    void getAll_postNotFound() {
        when(cacheRepository.get(postKey, PostDto.class)).thenReturn(Optional.empty());

        List<PostDto> result = postCacheService.getAll(postId);

        assertNotNull(result);
        assertNull(result.get(0));
    }

    @Test
    void saveAll_success() {
        PostDto post1 = new PostDto();
        post1.setId(1L);
        PostDto post2 = new PostDto();
        post2.setId(2L);

        List<PostDto> posts = List.of(post1, post2);
        Map<String, PostDto> expectedMap = Map.of(
                "1::post", post1,
                "2::post", post2
        );

        postCacheService.saveAll(posts);

        verify(cacheRepository).multiSetIfAbsent(expectedMap);
    }

    @Test
    void saveAll_emptyList() {
        List<PostDto> posts = Collections.emptyList();

        postCacheService.saveAll(posts);

        verify(cacheRepository).multiSetIfAbsent(Collections.emptyMap());
    }
}

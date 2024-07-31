package faang.school.postservice.service.feed.heat;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AddInfoAboutPostTest {

    @InjectMocks
    private AddInfoAboutPost addInfoAboutPost;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private RedisCacheService redisCacheService;

    @Value("${spring.data.redis.directory.post}")
    private String patternByPost = "post:";

    private Post post;
    private PostDto postDto;

    @BeforeEach
    public void setUp() {
        post = Post.builder().id(1L).content("Test Post").content("Content").build();
        postDto = PostDto.builder().id(1L).content("Test Post").content("Content").build();
        ReflectionTestUtils.setField(addInfoAboutPost, "patternByPost", patternByPost);
    }

    @Test
    public void testAddInfoToRedis() {
        Long userId = 1L;
        Long postId = 1L;

        List<Post> postList = List.of(post);
        List<PostDto> postDtoList = List.of(postDto);

        when(postRepository.findAllPublishedPostByID(postId)).thenReturn(postList);
        when(postMapper.toDto(any(Post.class))).thenReturn(postDto);

        addInfoAboutPost.addInfoToRedis(userId, postId);

        verify(postRepository, times(1)).findAllPublishedPostByID(postId);
        verify(postMapper, times(1)).toDto(any(Post.class));
        verify(redisCacheService, times(1)).saveToCache(patternByPost, postDto.getId(), postDto);
    }

    @Test
    public void testAddInfoToRedisWithEmptyList() {
        Long userId = 1L;
        Long postId = 1L;

        when(postRepository.findAllPublishedPostByID(postId)).thenReturn(List.of());

        addInfoAboutPost.addInfoToRedis(userId, postId);

        verify(postRepository, times(1)).findAllPublishedPostByID(postId);
        verify(postMapper, times(0)).toDto(any(Post.class));
        verify(redisCacheService, times(0)).saveToCache(any(String.class), any(Long.class), any(PostDto.class));
    }
}

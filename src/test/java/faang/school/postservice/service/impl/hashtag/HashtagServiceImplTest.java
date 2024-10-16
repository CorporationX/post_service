package faang.school.postservice.service.impl.hashtag;

import faang.school.postservice.model.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.hashtag.HashtagRepository;
import faang.school.postservice.service.impl.hashtag.HashtagServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.anyLong;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HashtagServiceImplTest {

    @Mock
    private HashtagRepository hashtagRepository;

    @Captor
    ArgumentCaptor<List<Hashtag>> hashtagListCaptor;

    @Captor
    ArgumentCaptor<List<PostDto>> postsDtoCaptor;

    @Mock
    private RedisTemplate<String, List<PostDto>> redisTemplate;

    @Mock
    private ValueOperations<String, List<PostDto>> valueOperations;

    @Spy
    private PostMapperImpl postMapper;

    @InjectMocks
    private HashtagServiceImpl hashtagService;

    @Test
    void testCreateHashtags() {
        Post post = Post.builder().content("sdgdgdfg #123").build();

        hashtagService.createHashtags(post);

        verify(hashtagRepository).saveAll(hashtagListCaptor.capture());

        assertEquals("123", hashtagListCaptor.getValue().get(0).getName());
    }

    @Test
    void testCreateHashtagsEmptyContentOk() {
        Post post = Post.builder().build();

        hashtagService.createHashtags(post);

        verify(hashtagRepository).saveAll(any());
    }

    @Test
    void testUpdateHashtagsOk() {
        Post post = Post.builder()
                .content("#another asd #hash")
                .hashtags(new ArrayList<>(List.of(Hashtag.builder().name("#pups").build())))
                .build();

        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        hashtagService.updateHashtags(post);

        assertEquals(2, post.getHashtags().size());
    }

    @Test
    void findPostsByHashtagOk() {
        Post post1 = Post.builder().id(1L).publishedAt(LocalDateTime.now().minusDays(3)).build();
        Post post2 = Post.builder().id(2L).publishedAt(LocalDateTime.now()).build();
        Hashtag hashtag = Hashtag.builder().name("#rere").posts(List.of(post1, post2)).build();

        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(hashtagRepository.findByName(anyString()))
                .thenReturn(Optional.of(hashtag));

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), postsDtoCaptor.capture());
        when(redisTemplate.expire(anyString(), anyLong(), any(TimeUnit.class))).thenReturn(false);

        List<PostDto> posts = hashtagService.findPostsByHashtag("asd");

        assertEquals(2, posts.size());
        assertEquals(2L, posts.get(0).id());
    }
}

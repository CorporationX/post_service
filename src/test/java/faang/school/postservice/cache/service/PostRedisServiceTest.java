package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CommentRedis;
import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.repository.PostRedisRepository;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = PostRedisService.class)
@ExtendWith(MockitoExtension.class)
class PostRedisServiceTest {
    @SpyBean
    @Autowired
    private PostRedisService postRedisService;
    @MockBean
    private PostRedisRepository postRedisRepository;
    @MockBean
    private PostMapper postMapper;
    @MockBean
    private RedisConcurrentExecutor redisConcurrentExecutor;
    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;
    @Captor
    private ArgumentCaptor<PostRedis> postRedisCaptor;

    @Value("${spring.data.redis.cache.post.comments.max-size}")
    private int commentsMaxSize;
    @Value("${spring.data.redis.cache.post.prefix}")
    private String postPrefix;

    private List<Long> ids;
    private PostRedis postRedis;
    private Iterable<PostRedis> postRedisIterable;
    private Post post;
    private CommentRedis commentRedis;
    private String key;
    private Long views;

    @BeforeEach
    void setUp() {
        ids = List.of(1L, 2L, 3L);
        postRedis = PostRedis.builder().id(1L).build();
        postRedisIterable = List.of(
                postRedis,
                PostRedis.builder().id(2L).build(),
                PostRedis.builder().id(3L).build()
        );
        post = Post.builder().id(1L).build();
        commentRedis = CommentRedis.builder().id(10L).postId(postRedis.getId()).build();
        key = postPrefix + postRedis.getId();
        views = 100L;
    }

    @Test
    void testGetAllByIds() {
        when(postRedisRepository.findAllById(ids)).thenReturn(postRedisIterable);

        List<PostRedis> actual = postRedisService.getAllByIds(ids);

        assertEquals(postRedisIterable, actual);
        verify(postRedisRepository, times(1)).findAllById(ids);
    }

    @Test
    void testSave() {
        when(postMapper.toRedis(post)).thenReturn(postRedis);

        postRedisService.save(post);

        verify(postMapper, times(1)).toRedis(post);
        verify(postRedisRepository, times(1)).save(postRedis);
    }

    @Test
    void testSaveAll() {
        postRedisService.saveAll(postRedisIterable);

        verify(postRedisRepository, times(1)).saveAll(postRedisIterable);
    }

    @Test
    void testUpdateIfExistsWhenExistsAndPublished() {
        post.setPublished(true);
        when(postRedisRepository.existsById(post.getId())).thenReturn(true);
        when(postRedisRepository.findById(post.getId())).thenReturn(Optional.of(postRedis));
        when(postMapper.toRedis(post)).thenReturn(postRedis);

        postRedisService.updateIfExists(post);

        verify(postRedisRepository, times(1)).existsById(post.getId());
        verify(postRedisRepository, times(1)).findById(post.getId());
        verify(postMapper, times(1)).toRedis(post);
        verify(postRedisRepository, times(1)).save(postRedis);
    }

    @Test
    void testUpdateIfExistsWhenNotExistsAndPublished() {
        post.setPublished(true);
        when(postRedisRepository.existsById(post.getId())).thenReturn(false);

        postRedisService.updateIfExists(post);

        verify(postRedisRepository, times(1)).existsById(post.getId());
        verify(postRedisRepository, times(0)).findById(anyLong());
        verify(postRedisRepository, times(0)).save(any(PostRedis.class));
    }

    @Test
    void testUpdateIfExistsWhenNotPublished() {
        post.setPublished(false);

        postRedisService.updateIfExists(post);

        verify(postRedisRepository, times(0)).existsById(anyLong());
        verify(postRedisRepository, times(0)).findById(anyLong());
        verify(postRedisRepository, times(0)).save(any(PostRedis.class));
    }

    @Test
    void testDeleteIfExistsWhenExists() {
        when(postRedisRepository.existsById(post.getId())).thenReturn(true);

        postRedisService.deleteIfExists(post.getId());

        verify(postRedisRepository, times(1)).existsById(post.getId());
        verify(postRedisRepository, times(1)).deleteById(post.getId());
    }

    @Test
    void testDeleteIfExistsWhenNotExists() {
        when(postRedisRepository.existsById(post.getId())).thenReturn(false);

        postRedisService.deleteIfExists(post.getId());

        verify(postRedisRepository, times(1)).existsById(post.getId());
        verify(postRedisRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void testExistsByIdWhenExists() {
        when(postRedisRepository.existsById(post.getId())).thenReturn(true);

        assertTrue(postRedisService.existsById(post.getId()));
    }

    @Test
    void testExistsByIdWhenNotExists() {
        when(postRedisRepository.existsById(post.getId())).thenReturn(false);

        assertFalse(postRedisService.existsById(post.getId()));
    }

    @Test
    void testFindById() {
        when(postRedisRepository.findById(postRedis.getId())).thenReturn(Optional.of(postRedis));

        PostRedis actual = postRedisService.findById(postRedis.getId());

        assertEquals(postRedis, actual);
        verify(postRedisRepository, times(1)).findById(postRedis.getId());
    }

    @Test
    void testFindByIdWhenNotFound() {
        when(postRedisRepository.findById(postRedis.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postRedisService.findById(postRedis.getId()));
        verify(postRedisRepository, times(1)).findById(postRedis.getId());
    }

    @Test
    void testAddCommentConcurrent() {
        when(postRedisRepository.existsById(postRedis.getId())).thenReturn(true);
        doNothing().when(postRedisService).addComment(any(CommentRedis.class));

        postRedisService.addCommentConcurrent(commentRedis);

        verify(postRedisRepository, times(1)).existsById(postRedis.getId());
        verify(redisConcurrentExecutor, times(1))
                .execute(eq(key), runnableCaptor.capture(), anyString());
        runnableCaptor.getValue().run();
        verify(postRedisService, times(1)).addComment(commentRedis);
    }

    @Test
    void testAddCommentConcurrentWhenPostNotExists() {
        when(postRedisRepository.existsById(postRedis.getId())).thenReturn(false);

        postRedisService.addCommentConcurrent(commentRedis);

        verify(postRedisRepository, times(1)).existsById(postRedis.getId());
        verify(redisConcurrentExecutor, times(0))
                .execute(anyString(), any(Runnable.class), anyString());
    }

    @Test
    void testAddCommentWhenPostWithoutComments() {
        when(postRedisRepository.findById(postRedis.getId())).thenReturn(Optional.of(postRedis));

        postRedisService.addComment(commentRedis);

        verify(postRedisRepository, times(1)).findById(postRedis.getId());
        verify(postRedisRepository, times(1)).save(postRedisCaptor.capture());
        PostRedis capturedPost = postRedisCaptor.getValue();
        assertTrue(capturedPost.getComments().contains(commentRedis));
    }

    @Test
    void testAddCommentWhenPostHaveMaxSizeComments() {
        TreeSet<CommentRedis> comments = new TreeSet<>(Set.of(
                CommentRedis.builder().id(2L).build(),
                CommentRedis.builder().id(7L).build(),
                CommentRedis.builder().id(5L).build()
        ));
        assertTrue(comments.size() >= commentsMaxSize);
        postRedis.setComments(comments);
        when(postRedisRepository.findById(postRedis.getId())).thenReturn(Optional.of(postRedis));

        postRedisService.addComment(commentRedis);

        verify(postRedisRepository, times(1)).findById(postRedis.getId());
        verify(postRedisRepository, times(1)).save(postRedisCaptor.capture());
        PostRedis capturedPost = postRedisCaptor.getValue();
        assertTrue(capturedPost.getComments().contains(commentRedis));
        assertEquals(commentsMaxSize, capturedPost.getComments().size());
    }

    @Test
    void testUpdateViewsConcurrent() {
        when(postRedisRepository.existsById(postRedis.getId())).thenReturn(true);
        doNothing().when(postRedisService).updateViews(anyLong(), anyLong());

        postRedisService.updateViewsConcurrent(postRedis.getId(), views);

        verify(postRedisRepository, times(1)).existsById(postRedis.getId());
        verify(redisConcurrentExecutor, times(1))
                .execute(eq(key), runnableCaptor.capture(), anyString());
        runnableCaptor.getValue().run();
        verify(postRedisService, times(1)).updateViews(postRedis.getId(), views);
    }

    @Test
    void testUpdateViewsConcurrentWhenPostNotExists() {
        when(postRedisRepository.existsById(postRedis.getId())).thenReturn(false);

        postRedisService.updateViewsConcurrent(postRedis.getId(), views);

        verify(postRedisRepository, times(1)).existsById(postRedis.getId());
        verify(redisConcurrentExecutor, times(0))
                .execute(anyString(), any(Runnable.class), anyString());
    }

    @Test
    void testUpdateViews() {
        when(postRedisRepository.findById(postRedis.getId())).thenReturn(Optional.of(postRedis));

        postRedisService.updateViews(postRedis.getId(), views);

        verify(postRedisRepository, times(1)).findById(postRedis.getId());
        verify(postRedisRepository, times(1)).save(postRedisCaptor.capture());
        PostRedis capturedPost = postRedisCaptor.getValue();
        assertEquals(views, capturedPost.getViews());
    }
}
package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CommentRedis;
import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.cache.repository.PostRedisRepository;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.CommentService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
    @MockBean
    private CommentService commentService;
    @MockBean
    private UserRedisService userRedisService;
    @MockBean
    private UserServiceClient userServiceClient;
    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;
    @Captor
    private ArgumentCaptor<PostRedis> postRedisCaptor;

    @Value("${spring.data.redis.cache.post.comments.max-size}")
    private int commentsMaxSize;
    @Value("${spring.data.redis.cache.post.prefix}")
    private String postPrefix;

    private List<Long> postIds;
    private UserRedis firstAuthorWithoutName, secondAuthorWithoutName, thirdAuthorWithoutName, fourthAuthorWithoutName,
            firstAuthorWithName, secondAuthorWithName, thirdAuthorWithName, fourthAuthorWithName;
    private Set<Long> authorIds;
    private List<UserRedis> authorsWithNamesList;
    private PostRedis firstPostRedis, secondPostRedis, thirdPostRedis;
    private List<PostRedis> postRedisList;
    private Iterable<PostRedis> postRedisIterable;
    private TreeSet<PostRedis> postRedisTreeSet;
    private Post post;
    private CommentRedis firstCommentRedis, secondCommentRedis, thirdCommentRedis;
    private List<CommentRedis> commentRedisList;
    private String key;
    private Long views;

    @BeforeEach
    void setUp() {
        postIds = List.of(1L, 2L);

        firstAuthorWithoutName = UserRedis.builder().id(1L).build();
        secondAuthorWithoutName = UserRedis.builder().id(2L).build();
        thirdAuthorWithoutName = UserRedis.builder().id(3L).build();
        fourthAuthorWithoutName = UserRedis.builder().id(4L).build();
        firstAuthorWithName = new UserRedis(1L, "user1");
        secondAuthorWithName = new UserRedis(2L, "user2");
        thirdAuthorWithName = new UserRedis(3L, "user3");
        fourthAuthorWithName = new UserRedis(4L, "user4");
        authorsWithNamesList = new ArrayList<>(Arrays
                .asList(firstAuthorWithName, secondAuthorWithName, thirdAuthorWithName, fourthAuthorWithName));
        authorIds = new HashSet<>();
        authorsWithNamesList.forEach(author -> authorIds.add(author.getId()));

        firstPostRedis = PostRedis.builder()
                .id(1L)
                .author(firstAuthorWithoutName)
                .build();
        secondPostRedis = PostRedis.builder()
                .id(2L)
                .author(secondAuthorWithoutName)
                .build();
        thirdPostRedis = PostRedis.builder()
                .id(3L)
                .author(fourthAuthorWithoutName)
                .build();

        firstCommentRedis = CommentRedis.builder()
                .id(10L)
                .postId(firstPostRedis.getId())
                .author(secondAuthorWithoutName)
                .build();
        secondCommentRedis = CommentRedis.builder()
                .id(12L)
                .postId(secondPostRedis.getId())
                .author(thirdAuthorWithoutName)
                .build();
        thirdCommentRedis = CommentRedis.builder()
                .id(18L)
                .postId(secondPostRedis.getId())
                .author(firstAuthorWithoutName)
                .build();
        commentRedisList = List.of(firstCommentRedis, secondCommentRedis, thirdCommentRedis);

        firstPostRedis.setComments(new TreeSet<>(Set.of(firstCommentRedis)));
        secondPostRedis.setComments(new TreeSet<>(Set.of(secondCommentRedis, thirdCommentRedis)));
        postRedisList = List.of(firstPostRedis, secondPostRedis, thirdPostRedis);
        postRedisIterable = postRedisList.stream().toList();
        postRedisTreeSet = new TreeSet<>(postRedisList);
        post = Post.builder().id(1L).build();
        key = postPrefix + firstPostRedis.getId();
        views = 100L;
    }

    @Test
    void testGetAllByIds() {
        when(postRedisRepository.findAllById(postIds)).thenReturn(postRedisIterable);

        List<PostRedis> actual = postRedisService.getAllByIds(postIds);

        assertEquals(postRedisIterable, actual);
        verify(postRedisRepository, times(1)).findAllById(postIds);
    }

    @Test
    void testSave() {
        when(postMapper.toRedis(post)).thenReturn(firstPostRedis);

        postRedisService.save(post);

        verify(postMapper, times(1)).toRedis(post);
        verify(postRedisRepository, times(1)).save(firstPostRedis);
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
        when(postRedisRepository.findById(post.getId())).thenReturn(Optional.of(firstPostRedis));
        when(postMapper.toRedis(post)).thenReturn(firstPostRedis);

        postRedisService.updateIfExists(post);

        verify(postRedisRepository, times(1)).existsById(post.getId());
        verify(postRedisRepository, times(1)).findById(post.getId());
        verify(postMapper, times(1)).toRedis(post);
        verify(postRedisRepository, times(1)).save(firstPostRedis);
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
        when(postRedisRepository.findById(firstPostRedis.getId())).thenReturn(Optional.of(firstPostRedis));

        PostRedis actual = postRedisService.findById(firstPostRedis.getId());

        assertEquals(firstPostRedis, actual);
        verify(postRedisRepository, times(1)).findById(firstPostRedis.getId());
    }

    @Test
    void testFindByIdWhenNotFound() {
        when(postRedisRepository.findById(firstPostRedis.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postRedisService.findById(firstPostRedis.getId()));
        verify(postRedisRepository, times(1)).findById(firstPostRedis.getId());
    }

    @Test
    void testAddCommentConcurrent() {
        when(postRedisRepository.existsById(firstPostRedis.getId())).thenReturn(true);
        doNothing().when(postRedisService).addComment(any(CommentRedis.class));

        postRedisService.addCommentConcurrent(firstCommentRedis);

        verify(postRedisRepository, times(1)).existsById(firstPostRedis.getId());
        verify(redisConcurrentExecutor, times(1))
                .execute(eq(key), runnableCaptor.capture(), anyString());
        runnableCaptor.getValue().run();
        verify(postRedisService, times(1)).addComment(firstCommentRedis);
    }

    @Test
    void testAddCommentConcurrentWhenPostNotExists() {
        when(postRedisRepository.existsById(firstPostRedis.getId())).thenReturn(false);

        postRedisService.addCommentConcurrent(firstCommentRedis);

        verify(postRedisRepository, times(1)).existsById(firstPostRedis.getId());
        verify(redisConcurrentExecutor, times(0))
                .execute(anyString(), any(Runnable.class), anyString());
    }

    @Test
    void testAddCommentWhenPostWithoutComments() {
        when(postRedisRepository.findById(firstPostRedis.getId())).thenReturn(Optional.of(firstPostRedis));

        postRedisService.addComment(firstCommentRedis);

        verify(postRedisRepository, times(1)).findById(firstPostRedis.getId());
        verify(postRedisRepository, times(1)).save(postRedisCaptor.capture());
        PostRedis capturedPost = postRedisCaptor.getValue();
        assertTrue(capturedPost.getComments().contains(firstCommentRedis));
    }

    @Test
    void testAddCommentWhenPostHaveMaxSizeComments() {
        TreeSet<CommentRedis> comments = new TreeSet<>(Set.of(
                CommentRedis.builder().id(2L).build(),
                CommentRedis.builder().id(7L).build(),
                CommentRedis.builder().id(5L).build()
        ));
        assertTrue(comments.size() >= commentsMaxSize);
        firstPostRedis.setComments(comments);
        when(postRedisRepository.findById(firstPostRedis.getId())).thenReturn(Optional.of(firstPostRedis));

        postRedisService.addComment(firstCommentRedis);

        verify(postRedisRepository, times(1)).findById(firstPostRedis.getId());
        verify(postRedisRepository, times(1)).save(postRedisCaptor.capture());
        PostRedis capturedPost = postRedisCaptor.getValue();
        assertTrue(capturedPost.getComments().contains(firstCommentRedis));
        assertEquals(commentsMaxSize, capturedPost.getComments().size());
    }

    @Test
    void testUpdateViewsConcurrent() {
        when(postRedisRepository.existsById(firstPostRedis.getId())).thenReturn(true);
        doNothing().when(postRedisService).updateViews(anyLong(), anyLong());

        postRedisService.updateViewsConcurrent(firstPostRedis.getId(), views);

        verify(postRedisRepository, times(1)).existsById(firstPostRedis.getId());
        verify(redisConcurrentExecutor, times(1))
                .execute(eq(key), runnableCaptor.capture(), anyString());
        runnableCaptor.getValue().run();
        verify(postRedisService, times(1)).updateViews(firstPostRedis.getId(), views);
    }

    @Test
    void testUpdateViewsConcurrentWhenPostNotExists() {
        when(postRedisRepository.existsById(firstPostRedis.getId())).thenReturn(false);

        postRedisService.updateViewsConcurrent(firstPostRedis.getId(), views);

        verify(postRedisRepository, times(1)).existsById(firstPostRedis.getId());
        verify(redisConcurrentExecutor, times(0))
                .execute(anyString(), any(Runnable.class), anyString());
    }

    @Test
    void testUpdateViews() {
        when(postRedisRepository.findById(firstPostRedis.getId())).thenReturn(Optional.of(firstPostRedis));

        postRedisService.updateViews(firstPostRedis.getId(), views);

        verify(postRedisRepository, times(1)).findById(firstPostRedis.getId());
        verify(postRedisRepository, times(1)).save(postRedisCaptor.capture());
        PostRedis capturedPost = postRedisCaptor.getValue();
        assertEquals(views, capturedPost.getViews());
    }

    @Test
    void testSetCommentsFromDB() {
        when(commentService.findLastBatchByPostIds(commentsMaxSize, postIds)).thenReturn(commentRedisList);

        postRedisService.setCommentsFromDB(postRedisList);

        assertTrue(postRedisList.get(0).getComments().contains(firstCommentRedis));
        assertTrue(postRedisList.get(1).getComments().contains(secondCommentRedis));
        assertTrue(postRedisList.get(1).getComments().contains(thirdCommentRedis));
    }

    @Test
    void testSetAuthorsWhenAuthorsEnoughInCache() {
        when(postRedisService.extractUserIds(postRedisTreeSet)).thenReturn(authorIds);
        when(userRedisService.getAllByIds(authorIds)).thenReturn(authorsWithNamesList);

        postRedisService.setAuthors(postRedisTreeSet);

        List<PostRedis> result = postRedisTreeSet.stream().toList();
        assertEquals(fourthAuthorWithName, result.get(0).getAuthor());
        assertEquals(secondAuthorWithName, result.get(1).getAuthor());
        assertEquals(firstAuthorWithName, result.get(1).getComments().pollFirst().getAuthor());
        assertEquals(thirdAuthorWithName, result.get(1).getComments().pollFirst().getAuthor());
        assertEquals(firstAuthorWithName, result.get(2).getAuthor());
        assertEquals(secondAuthorWithName, result.get(2).getComments().pollFirst().getAuthor());
    }

    @Test
    void testSetAuthorsWhenSomeAuthorsExpiredInCache() {
        List<UserRedis> expiredUsers = new ArrayList<>();
        expiredUsers.add(authorsWithNamesList.remove(0));
        expiredUsers.add(authorsWithNamesList.remove(1));
        List<Long> expiredUserIds = expiredUsers.stream().map(UserRedis::getId).toList();
        List<UserDto> expiredUserDtos = expiredUsers.stream()
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .build())
                .toList();
        when(postRedisService.extractUserIds(postRedisTreeSet)).thenReturn(authorIds);
        when(userRedisService.getAllByIds(authorIds)).thenReturn(authorsWithNamesList);
        when(userServiceClient.getUsersByIds(expiredUserIds)).thenReturn(expiredUserDtos);

        postRedisService.setAuthors(postRedisTreeSet);

        List<PostRedis> result = postRedisTreeSet.stream().toList();
        assertEquals(fourthAuthorWithName, result.get(0).getAuthor());
        assertEquals(secondAuthorWithName, result.get(1).getAuthor());
        assertEquals(firstAuthorWithName, result.get(1).getComments().pollFirst().getAuthor());
        assertEquals(thirdAuthorWithName, result.get(1).getComments().pollFirst().getAuthor());
        assertEquals(firstAuthorWithName, result.get(2).getAuthor());
        assertEquals(secondAuthorWithName, result.get(2).getComments().pollFirst().getAuthor());
    }

    @Test
    void testExtractUserIds() {
        Set<Long> actual = postRedisService.extractUserIds(postRedisTreeSet);

        assertEquals(authorIds, actual);
    }
}
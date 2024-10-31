package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CacheableComment;
import faang.school.postservice.cache.model.CacheablePost;
import faang.school.postservice.cache.model.CacheableUser;
import faang.school.postservice.cache.repository.CacheablePostRepository;
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

@SpringBootTest(classes = CacheablePostService.class)
@ExtendWith(MockitoExtension.class)
class CacheablePostServiceTest {
    @SpyBean
    @Autowired
    private CacheablePostService cacheablePostService;
    @MockBean
    private CacheablePostRepository cacheablePostRepository;
    @MockBean
    private PostMapper postMapper;
    @MockBean
    private RedisConcurrentExecutor redisConcurrentExecutor;
    @MockBean
    private CommentService commentService;
    @MockBean
    private CacheableUserService cacheableUserService;
    @MockBean
    private UserServiceClient userServiceClient;
    @Captor
    private ArgumentCaptor<Runnable> runnableCaptor;
    @Captor
    private ArgumentCaptor<CacheablePost> cacheablePostCaptor;

    @Value("${spring.data.redis.cache.post.comments.max-size}")
    private int commentsMaxSize;
    @Value("${spring.data.redis.cache.post.prefix}")
    private String postPrefix;

    private List<Long> postIds;
    private CacheableUser firstAuthorWithoutName, secondAuthorWithoutName, thirdAuthorWithoutName, fourthAuthorWithoutName,
            firstAuthorWithName, secondAuthorWithName, thirdAuthorWithName, fourthAuthorWithName;
    private Set<Long> authorIds;
    private List<CacheableUser> authorsWithNamesList;
    private CacheablePost firstCacheablePost, secondCacheablePost, thirdCacheablePost;
    private List<CacheablePost> cacheablePostList;
    private Iterable<CacheablePost> cacheablePostIterable;
    private TreeSet<CacheablePost> cacheablePostTreeSet;
    private Post post;
    private CacheableComment firstCacheableComment, secondCacheableComment, thirdCacheableComment;
    private List<CacheableComment> cacheableCommentList;
    private String key;
    private Long views;

    @BeforeEach
    void setUp() {
        postIds = List.of(1L, 2L);

        firstAuthorWithoutName = CacheableUser.builder().id(1L).build();
        secondAuthorWithoutName = CacheableUser.builder().id(2L).build();
        thirdAuthorWithoutName = CacheableUser.builder().id(3L).build();
        fourthAuthorWithoutName = CacheableUser.builder().id(4L).build();
        firstAuthorWithName = new CacheableUser(1L, "user1");
        secondAuthorWithName = new CacheableUser(2L, "user2");
        thirdAuthorWithName = new CacheableUser(3L, "user3");
        fourthAuthorWithName = new CacheableUser(4L, "user4");
        authorsWithNamesList = new ArrayList<>(Arrays
                .asList(firstAuthorWithName, secondAuthorWithName, thirdAuthorWithName, fourthAuthorWithName));
        authorIds = new HashSet<>();
        authorsWithNamesList.forEach(author -> authorIds.add(author.getId()));

        firstCacheablePost = CacheablePost.builder()
                .id(1L)
                .author(firstAuthorWithoutName)
                .build();
        secondCacheablePost = CacheablePost.builder()
                .id(2L)
                .author(secondAuthorWithoutName)
                .build();
        thirdCacheablePost = CacheablePost.builder()
                .id(3L)
                .author(fourthAuthorWithoutName)
                .build();

        firstCacheableComment = CacheableComment.builder()
                .id(10L)
                .postId(firstCacheablePost.getId())
                .author(secondAuthorWithoutName)
                .build();
        secondCacheableComment = CacheableComment.builder()
                .id(12L)
                .postId(secondCacheablePost.getId())
                .author(thirdAuthorWithoutName)
                .build();
        thirdCacheableComment = CacheableComment.builder()
                .id(18L)
                .postId(secondCacheablePost.getId())
                .author(firstAuthorWithoutName)
                .build();
        cacheableCommentList = List.of(firstCacheableComment, secondCacheableComment, thirdCacheableComment);

        firstCacheablePost.setComments(new TreeSet<>(Set.of(firstCacheableComment)));
        secondCacheablePost.setComments(new TreeSet<>(Set.of(secondCacheableComment, thirdCacheableComment)));
        cacheablePostList = List.of(firstCacheablePost, secondCacheablePost, thirdCacheablePost);
        cacheablePostIterable = cacheablePostList.stream().toList();
        cacheablePostTreeSet = new TreeSet<>(cacheablePostList);
        post = Post.builder().id(1L).build();
        key = postPrefix + firstCacheablePost.getId();
        views = 100L;
    }

    @Test
    void testGetAllByIds() {
        when(cacheablePostRepository.findAllById(postIds)).thenReturn(cacheablePostIterable);

        List<CacheablePost> actual = cacheablePostService.getAllByIds(postIds);

        assertEquals(cacheablePostIterable, actual);
        verify(cacheablePostRepository, times(1)).findAllById(postIds);
    }

    @Test
    void testSave() {
        when(postMapper.toCacheable(post)).thenReturn(firstCacheablePost);

        cacheablePostService.save(post);

        verify(postMapper, times(1)).toCacheable(post);
        verify(cacheablePostRepository, times(1)).save(firstCacheablePost);
    }

    @Test
    void testSaveAll() {
        cacheablePostService.saveAll(cacheablePostIterable);

        verify(cacheablePostRepository, times(1)).saveAll(cacheablePostIterable);
    }

    @Test
    void testUpdateIfExistsWhenExistsAndPublished() {
        post.setPublished(true);
        when(cacheablePostRepository.existsById(post.getId())).thenReturn(true);
        when(cacheablePostRepository.findById(post.getId())).thenReturn(Optional.of(firstCacheablePost));
        when(postMapper.toCacheable(post)).thenReturn(firstCacheablePost);

        cacheablePostService.updateIfExists(post);

        verify(cacheablePostRepository, times(1)).existsById(post.getId());
        verify(cacheablePostRepository, times(1)).findById(post.getId());
        verify(postMapper, times(1)).toCacheable(post);
        verify(cacheablePostRepository, times(1)).save(firstCacheablePost);
    }

    @Test
    void testUpdateIfExistsWhenNotExistsAndPublished() {
        post.setPublished(true);
        when(cacheablePostRepository.existsById(post.getId())).thenReturn(false);

        cacheablePostService.updateIfExists(post);

        verify(cacheablePostRepository, times(1)).existsById(post.getId());
        verify(cacheablePostRepository, times(0)).findById(anyLong());
        verify(cacheablePostRepository, times(0)).save(any(CacheablePost.class));
    }

    @Test
    void testUpdateIfExistsWhenNotPublished() {
        post.setPublished(false);

        cacheablePostService.updateIfExists(post);

        verify(cacheablePostRepository, times(0)).existsById(anyLong());
        verify(cacheablePostRepository, times(0)).findById(anyLong());
        verify(cacheablePostRepository, times(0)).save(any(CacheablePost.class));
    }

    @Test
    void testDeleteIfExistsWhenExists() {
        when(cacheablePostRepository.existsById(post.getId())).thenReturn(true);

        cacheablePostService.deleteIfExists(post.getId());

        verify(cacheablePostRepository, times(1)).existsById(post.getId());
        verify(cacheablePostRepository, times(1)).deleteById(post.getId());
    }

    @Test
    void testDeleteIfExistsWhenNotExists() {
        when(cacheablePostRepository.existsById(post.getId())).thenReturn(false);

        cacheablePostService.deleteIfExists(post.getId());

        verify(cacheablePostRepository, times(1)).existsById(post.getId());
        verify(cacheablePostRepository, times(0)).deleteById(anyLong());
    }

    @Test
    void testExistsByIdWhenExists() {
        when(cacheablePostRepository.existsById(post.getId())).thenReturn(true);

        assertTrue(cacheablePostService.existsById(post.getId()));
    }

    @Test
    void testExistsByIdWhenNotExists() {
        when(cacheablePostRepository.existsById(post.getId())).thenReturn(false);

        assertFalse(cacheablePostService.existsById(post.getId()));
    }

    @Test
    void testFindById() {
        when(cacheablePostRepository.findById(firstCacheablePost.getId())).thenReturn(Optional.of(firstCacheablePost));

        CacheablePost actual = cacheablePostService.findById(firstCacheablePost.getId());

        assertEquals(firstCacheablePost, actual);
        verify(cacheablePostRepository, times(1)).findById(firstCacheablePost.getId());
    }

    @Test
    void testFindByIdWhenNotFound() {
        when(cacheablePostRepository.findById(firstCacheablePost.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cacheablePostService.findById(firstCacheablePost.getId()));
        verify(cacheablePostRepository, times(1)).findById(firstCacheablePost.getId());
    }

    @Test
    void testAddCommentConcurrent() {
        when(cacheablePostRepository.existsById(firstCacheablePost.getId())).thenReturn(true);
        doNothing().when(cacheablePostService).addComment(any(CacheableComment.class));

        cacheablePostService.addCommentConcurrent(firstCacheableComment);

        verify(cacheablePostRepository, times(1)).existsById(firstCacheablePost.getId());
        verify(redisConcurrentExecutor, times(1))
                .execute(eq(key), runnableCaptor.capture(), anyString());
        runnableCaptor.getValue().run();
        verify(cacheablePostService, times(1)).addComment(firstCacheableComment);
    }

    @Test
    void testAddCommentConcurrentWhenPostNotExists() {
        when(cacheablePostRepository.existsById(firstCacheablePost.getId())).thenReturn(false);

        cacheablePostService.addCommentConcurrent(firstCacheableComment);

        verify(cacheablePostRepository, times(1)).existsById(firstCacheablePost.getId());
        verify(redisConcurrentExecutor, times(0))
                .execute(anyString(), any(Runnable.class), anyString());
    }

    @Test
    void testAddCommentWhenPostWithoutComments() {
        when(cacheablePostRepository.findById(firstCacheablePost.getId())).thenReturn(Optional.of(firstCacheablePost));

        cacheablePostService.addComment(firstCacheableComment);

        verify(cacheablePostRepository, times(1)).findById(firstCacheablePost.getId());
        verify(cacheablePostRepository, times(1)).save(cacheablePostCaptor.capture());
        CacheablePost capturedPost = cacheablePostCaptor.getValue();
        assertTrue(capturedPost.getComments().contains(firstCacheableComment));
    }

    @Test
    void testAddCommentWhenPostHaveMaxSizeComments() {
        TreeSet<CacheableComment> comments = new TreeSet<>(Set.of(
                CacheableComment.builder().id(2L).build(),
                CacheableComment.builder().id(7L).build(),
                CacheableComment.builder().id(5L).build()
        ));
        assertTrue(comments.size() >= commentsMaxSize);
        firstCacheablePost.setComments(comments);
        when(cacheablePostRepository.findById(firstCacheablePost.getId())).thenReturn(Optional.of(firstCacheablePost));

        cacheablePostService.addComment(firstCacheableComment);

        verify(cacheablePostRepository, times(1)).findById(firstCacheablePost.getId());
        verify(cacheablePostRepository, times(1)).save(cacheablePostCaptor.capture());
        CacheablePost capturedPost = cacheablePostCaptor.getValue();
        assertTrue(capturedPost.getComments().contains(firstCacheableComment));
        assertEquals(commentsMaxSize, capturedPost.getComments().size());
    }

    @Test
    void testUpdateViewsConcurrent() {
        when(cacheablePostRepository.existsById(firstCacheablePost.getId())).thenReturn(true);
        doNothing().when(cacheablePostService).updateViews(anyLong(), anyLong());

        cacheablePostService.updateViewsConcurrent(firstCacheablePost.getId(), views);

        verify(cacheablePostRepository, times(1)).existsById(firstCacheablePost.getId());
        verify(redisConcurrentExecutor, times(1))
                .execute(eq(key), runnableCaptor.capture(), anyString());
        runnableCaptor.getValue().run();
        verify(cacheablePostService, times(1)).updateViews(firstCacheablePost.getId(), views);
    }

    @Test
    void testUpdateViewsConcurrentWhenPostNotExists() {
        when(cacheablePostRepository.existsById(firstCacheablePost.getId())).thenReturn(false);

        cacheablePostService.updateViewsConcurrent(firstCacheablePost.getId(), views);

        verify(cacheablePostRepository, times(1)).existsById(firstCacheablePost.getId());
        verify(redisConcurrentExecutor, times(0))
                .execute(anyString(), any(Runnable.class), anyString());
    }

    @Test
    void testUpdateViews() {
        when(cacheablePostRepository.findById(firstCacheablePost.getId())).thenReturn(Optional.of(firstCacheablePost));

        cacheablePostService.updateViews(firstCacheablePost.getId(), views);

        verify(cacheablePostRepository, times(1)).findById(firstCacheablePost.getId());
        verify(cacheablePostRepository, times(1)).save(cacheablePostCaptor.capture());
        CacheablePost capturedPost = cacheablePostCaptor.getValue();
        assertEquals(views, capturedPost.getViews());
    }

    @Test
    void testSetCommentsFromDB() {
        when(commentService.findLastBatchByPostIds(commentsMaxSize, postIds)).thenReturn(cacheableCommentList);

        cacheablePostService.setCommentsFromDB(cacheablePostList);

        assertTrue(cacheablePostList.get(0).getComments().contains(firstCacheableComment));
        assertTrue(cacheablePostList.get(1).getComments().contains(secondCacheableComment));
        assertTrue(cacheablePostList.get(1).getComments().contains(thirdCacheableComment));
    }

    @Test
    void testSetAuthorsWhenAuthorsEnoughInCache() {
        when(cacheablePostService.extractUserIds(cacheablePostTreeSet)).thenReturn(authorIds);
        when(cacheableUserService.getAllByIds(authorIds)).thenReturn(authorsWithNamesList);

        cacheablePostService.setAuthors(cacheablePostTreeSet);

        List<CacheablePost> result = cacheablePostTreeSet.stream().toList();
        assertEquals(fourthAuthorWithName, result.get(0).getAuthor());
        assertEquals(secondAuthorWithName, result.get(1).getAuthor());
        assertEquals(firstAuthorWithName, result.get(1).getComments().pollFirst().getAuthor());
        assertEquals(thirdAuthorWithName, result.get(1).getComments().pollFirst().getAuthor());
        assertEquals(firstAuthorWithName, result.get(2).getAuthor());
        assertEquals(secondAuthorWithName, result.get(2).getComments().pollFirst().getAuthor());
    }

    @Test
    void testSetAuthorsWhenSomeAuthorsExpiredInCache() {
        List<CacheableUser> expiredUsers = new ArrayList<>();
        expiredUsers.add(authorsWithNamesList.remove(0));
        expiredUsers.add(authorsWithNamesList.remove(1));
        List<Long> expiredUserIds = expiredUsers.stream().map(CacheableUser::getId).toList();
        List<UserDto> expiredUserDtos = expiredUsers.stream()
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .build())
                .toList();
        when(cacheablePostService.extractUserIds(cacheablePostTreeSet)).thenReturn(authorIds);
        when(cacheableUserService.getAllByIds(authorIds)).thenReturn(authorsWithNamesList);
        when(userServiceClient.getUsersByIds(expiredUserIds)).thenReturn(expiredUserDtos);

        cacheablePostService.setAuthors(cacheablePostTreeSet);

        List<CacheablePost> result = cacheablePostTreeSet.stream().toList();
        assertEquals(fourthAuthorWithName, result.get(0).getAuthor());
        assertEquals(secondAuthorWithName, result.get(1).getAuthor());
        assertEquals(firstAuthorWithName, result.get(1).getComments().pollFirst().getAuthor());
        assertEquals(thirdAuthorWithName, result.get(1).getComments().pollFirst().getAuthor());
        assertEquals(firstAuthorWithName, result.get(2).getAuthor());
        assertEquals(secondAuthorWithName, result.get(2).getComments().pollFirst().getAuthor());
    }

    @Test
    void testExtractUserIds() {
        Set<Long> actual = cacheablePostService.extractUserIds(cacheablePostTreeSet);

        assertEquals(authorIds, actual);
    }
}
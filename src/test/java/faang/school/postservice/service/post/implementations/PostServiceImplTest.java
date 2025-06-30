package faang.school.postservice.service.post.implementations;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostDtoValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post_check.implementations.PostCheckerServiceImpl;
import faang.school.postservice.utils.batch.PostEventBatchSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private PostCheckerServiceImpl postCheckerService;

    @Mock
    private PlatformTransactionManager transactionManager;

    @MockBean
    private ExecutorService postPublishPool;

    @Mock
    private PostEventBatchSender postEventBatchSender;

    @InjectMocks
    private PostServiceImpl postService;

    private PostDto inputDto;
    private Post entity;
    private PostDto outputDto;

    @BeforeEach
    void setUp() {
        inputDto = new PostDto();
        inputDto.setId(1L);
        inputDto.setContent("Draft content");

        entity = new Post();
        entity.setId(1L);
        entity.setContent("Draft content");

        outputDto = new PostDto();
        outputDto.setId(1L);
        outputDto.setContent("Draft content");
    }

    @Test
    void testCreatePostDraftSuccessWithUser() {
        inputDto.setAuthorId(1L);
        inputDto.setProjectId(0L);
        entity.setAuthorId(1L);
        entity.setProjectId(0L);
        outputDto.setAuthorId(1L);
        outputDto.setProjectId(0L);

        when(userServiceClient.getUser(1L)).thenReturn(new UserDto(1L, "User", "email"));
        when(postRepository.save(any(Post.class))).thenReturn(entity);

        PostDto result = postService.createPostDraft(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Draft content", result.getContent());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(userServiceClient, times(1)).getUser(1L);
        verify(projectServiceClient, never()).getProject(anyLong());
    }

    @Test
    void testCreatePostDraftSuccessWithProject() {
        inputDto.setAuthorId(0L);
        inputDto.setProjectId(1L);
        entity.setAuthorId(0L);
        entity.setProjectId(1L);
        outputDto.setAuthorId(0L);
        outputDto.setProjectId(1L);

        when(projectServiceClient.getProject(1L)).thenReturn(new ProjectDto(1L, "Project"));
        when(postRepository.save(any(Post.class))).thenReturn(entity);

        PostDto result = postService.createPostDraft(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Draft content", result.getContent());
        verify(postRepository, times(1)).save(any(Post.class));
        verify(projectServiceClient, times(1)).getProject(1L);
        verify(userServiceClient, never()).getUser(anyLong());
    }

    @Test
    void testCreatePostDraftNegativeAuthorId() {
        inputDto.setAuthorId(-1L);
        inputDto.setProjectId(0L);

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.createPostDraft(inputDto)
        );
        assertEquals("ID should not be less than zero!", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void testCreatePostDraftBothIdsZero() {
        inputDto.setAuthorId(0L);
        inputDto.setProjectId(0L);

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.createPostDraft(inputDto)
        );
        assertEquals("One author required!", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void testCreatePostDraftBothIdsNonZero() {
        inputDto.setAuthorId(1L);
        inputDto.setProjectId(1L);

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.createPostDraft(inputDto)
        );
        assertEquals("The author can be either a user or a project!", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void testCreatePostDraftUserNotFound() {
        inputDto.setAuthorId(1L);
        inputDto.setProjectId(0L);
        when(userServiceClient.getUser(1L)).thenReturn(new UserDto(0L, "Not Found", "none"));

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.createPostDraft(inputDto)
        );
        assertEquals("User with ID 1 not found!", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void testCreatePostDraftProjectNotFound() {
        inputDto.setAuthorId(0L);
        inputDto.setProjectId(1L);
        when(projectServiceClient.getProject(1L)).thenReturn(new ProjectDto(0L, "Not Found"));

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.createPostDraft(inputDto)
        );
        assertEquals("Project with ID 1 not found!", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    /****************************************************************************************/
    @Test
    void testPublicPostSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(postRepository.saveAndFlush(any(Post.class))).thenReturn(entity);
        when(postMapper.toDto(any(Post.class))).thenReturn(outputDto);

        PostDto result = postService.publishPost(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).saveAndFlush(any(Post.class));
        verify(postMapper, times(1)).toDto(any(Post.class));
    }

    @Test
    void testPublicPostPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PostDtoValidationException exception = assertThrows(PostDtoValidationException.class,
                () -> postService.publishPost(inputDto)
        );

        assertEquals("Post with ID 1 does not exist", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any());
        verify(postMapper, never()).toDto(any());
    }

    @Test
    void testPublicPostPostDeleted() {
        entity.setDeleted(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(entity));

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.publishPost(inputDto)
        );
        assertEquals("The post with ID 1 removed", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any());
        verify(postMapper, never()).toDto(any());
    }

    @Test
    void testPublicPostAlreadyPublished() {
        entity.setPublished(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(entity));

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.publishPost(inputDto)
        );
        assertEquals("The post with ID 1 has already been published", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any());
        verify(postMapper, never()).toDto(any());
    }

    /****************************************************************************************/
    @Test
    void testUpdatePostSuccess() {
        inputDto.setContent("Updated content");
        entity.setContent("Original content");
        when(postRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto result = postService.updatePost(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated content", result.getContent());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testUpdatePostPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.updatePost(inputDto)
        );
        assertEquals("Post with ID 1 does not exist", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any());
    }

    /****************************************************************************************/
    @Test
    void testDeletePostSuccess() {
        entity.setContent("Test content");
        when(postRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto result = postService.deletePost(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test content", result.getContent());
        assertTrue(result.isDeleted());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void testDeletePostPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.deletePost(inputDto)
        );
        assertEquals("Post with ID 1 does not exist", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, never()).save(any());
    }

    /****************************************************************************************/
    @Test
    void testGetPostSuccess() {
        entity.setContent("Test content");
        when(postRepository.findById(1L)).thenReturn(Optional.of(entity));

        PostDto result = postService.getPost(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test content", result.getContent());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPostPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        PostDtoValidationException exception = assertThrows(
                PostDtoValidationException.class,
                () -> postService.getPost(inputDto)
        );
        assertEquals("Post with ID 1 does not exist", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
    }

    /***************************************************************************************/
    @Test
    public void testGetAuthorPostDraftsSuccess() {
        Long authorId = 1L;
        PostDto inputDto = new PostDto();
        inputDto.setAuthorId(1L);

        Post draft1 = new Post();
        draft1.setId(1L);
        draft1.setAuthorId(authorId);
        draft1.setPublished(false);
        draft1.setDeleted(false);
        draft1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Post draft2 = new Post();
        draft2.setId(2L);
        draft2.setAuthorId(authorId);
        draft2.setPublished(false);
        draft2.setDeleted(false);
        draft2.setCreatedAt(LocalDateTime.now());

        Post published = new Post();
        published.setId(3L);
        published.setAuthorId(authorId);
        published.setPublished(true);
        published.setDeleted(false);
        published.setCreatedAt(LocalDateTime.now().minusDays(2));

        when(postRepository.findByAuthorId(authorId))
                .thenReturn(Arrays.asList(draft1, draft2, published));

        List<PostDto> result = postService.getAuthorPostDrafts(inputDto);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(1L, result.get(1).getId());
        verify(postRepository).findByAuthorId(authorId);
        verify(postMapper, times(2)).toDto(any(Post.class));
    }

    @Test
    public void testGetAuthorPostDraftsEmptyListReturnsEmpty() {
        Long authorId = 1L;
        PostDto inputDto = new PostDto();
        inputDto.setAuthorId(authorId);
        when(postRepository.findByAuthorId(authorId)).thenReturn(List.of());

        List<PostDto> result = postService.getAuthorPostDrafts(inputDto);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postRepository).findByAuthorId(authorId);
        verify(postMapper, never()).toDto(any(Post.class));
    }

    @Test
    public void testGetAuthorPublishedPostsSuccess() {
        Long authorId = 1L;
        PostDto inputDto = new PostDto();
        inputDto.setAuthorId(1L);

        Post published1 = new Post();
        published1.setId(1L);
        published1.setAuthorId(authorId);
        published1.setPublished(true);
        published1.setDeleted(false);
        published1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Post published2 = new Post();
        published2.setId(2L);
        published2.setAuthorId(authorId);
        published2.setPublished(true);
        published2.setDeleted(false);
        published2.setCreatedAt(LocalDateTime.now());

        Post draft = new Post();
        draft.setId(3L);
        draft.setAuthorId(authorId);
        draft.setPublished(false);
        draft.setDeleted(false);
        draft.setCreatedAt(LocalDateTime.now().minusDays(2));

        when(postRepository.findByAuthorId(authorId))
                .thenReturn(Arrays.asList(published1, published2, draft));

        List<PostDto> result = postService.getAuthorPublishedPosts(inputDto);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(1L, result.get(1).getId());
        verify(postRepository).findByAuthorId(authorId);
        verify(postMapper, times(2)).toDto(any(Post.class));
    }

    @Test
    void testCorrectUnpublishedPostsSuccessfully() {
        List<Post> unpublishedPosts = List.of(new Post(), new Post());
        when(postRepository.findReadyToPublish()).thenReturn(unpublishedPosts);
        when(postCheckerService.correctPost(any(Post.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        postService.correctUnpublishedPosts();

        verify(postCheckerService, times(2)).correctPost(any(Post.class));
    }

    @Test
    void testCorrectUnpublishedPosts_whenPostsReadyToPublishIsAbsent() {
        when(postRepository.findReadyToPublish()).thenReturn(Collections.emptyList());

        postService.correctUnpublishedPosts();

        verify(postCheckerService, never()).correctPost(any(Post.class));
    }

    /***************************************************************************************/
    private void setUpPublishScheduledPosts() {
        postPublishPool = new ThreadPoolExecutor(
                10,
                10,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(100),
                new ThreadPoolExecutor.AbortPolicy()
        );

        try {
            Field field = PostServiceImpl.class.getDeclaredField("postPublishPool");
            field.setAccessible(true);
            field.set(postService, postPublishPool);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set postPublishPool", e);
        }
    }

    @Test
    void testPublishScheduledPostsWithEmptyList() {
        when(postRepository.findReadyToPublish()).thenReturn(List.of());

        postService.publishScheduledPosts();

        verify(postRepository, never()).saveAll(anyList());
    }

    @Test
    void testPublishScheduledPostsWithPostsProcessesChunks() {
        setUpPublishScheduledPosts();
        List<Post> posts = new ArrayList<>();
        for (long i = 1; i <= 25; i++) {
            Post post = new Post();
            post.setId(i);
            post.setPublished(false);
            post.setDeleted(false);
            post.setScheduledAt(LocalDateTime.now().minusDays(1));
            posts.add(post);
        }

        when(postRepository.findReadyToPublish()).thenReturn(posts);
        when(postRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        postService.publishScheduledPosts().join();

        for (Post post : posts) {
            assertTrue(post.isPublished());
            assertNotNull(post.getPublishedAt());
            assertNull(post.getScheduledAt());
        }

        verify(postRepository, times(9)).saveAll(anyList());
    }
}

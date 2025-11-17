package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.GetUsersDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.UserBanEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.UserBanEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostServiceImpl;
import faang.school.postservice.service.user.UserServiceImpl;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {
    @InjectMocks
    private PostServiceImpl postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private ExecutorService scheduledPostExecutor;
    private UserBanEventPublisher userBanEventPublisher;
    @Mock
    private UserServiceImpl userService;
    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    private static final long DEFAULT_ID = 1L;
    private static final long DEFAULT_NEGATIVE_ID = -1L;
    private final long authorId = DEFAULT_ID;
    private final long projectId = DEFAULT_ID;
    private final long postId = DEFAULT_ID;
    private final long userId = DEFAULT_ID;
    private final LocalDateTime time1 = LocalDateTime.of(2024, 1, 1, 0, 0);
    private final LocalDateTime time2 = LocalDateTime.of(2024, 1, 2, 0, 0);
    private final int maxUnverifiedPosts = 5;
    private final int findUnverifiedPostsPageSize = maxUnverifiedPosts + 1;
  
    @BeforeEach
    void setUp() throws Exception {
        Field batchSizeField = PostServiceImpl.class.getDeclaredField("batchSize");
        batchSizeField.setAccessible(true);
        batchSizeField.set(postService, 2);
    }
    

    private final PostDto postDtoAuthorExists = PostDto.builder().id(postId).content("content")
            .authorId(authorId).projectId(null)
            .published(false).publishedAt(null).deleted(false)
            .createdAt(LocalDateTime.now()).updatedAt(null)
            .build();

    private final PostDto postDtoProjectExists = PostDto.builder().id(postId).content("content")
            .authorId(null).projectId(projectId)
            .published(false).publishedAt(null).deleted(false)
            .createdAt(LocalDateTime.now()).updatedAt(null)
            .build();

    private final UserDto mockUser = UserDto.builder()
            .id(DEFAULT_ID)
            .username("mockUser")
            .email("mockUser@example.com")
            .build();
    private final ProjectDto mockProject = new ProjectDto(projectId, "mockProject");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postService, "maxUnverifiedPosts", maxUnverifiedPosts);
        ReflectionTestUtils.setField(postService, "findUnverifiedPostsPageSize", findUnverifiedPostsPageSize);
    }

    @Test
    public void testCreateDraftWithoutAuthorAndProject() {
        CreatePostDto postDto = new CreatePostDto("content", null, null);

        assertThrows(DataValidationException.class, () -> postService.createDraft(postDto));
    }

    @Test
    public void testCreateDraftWithBothAuthorAndProject() {
        CreatePostDto postDto = new CreatePostDto("content", authorId, projectId);

        assertThrows(DataValidationException.class, () -> postService.createDraft((postDto)));
    }

    @Test
    public void testCreateDraftWithNegativeAuthorId() {
        CreatePostDto postDto = new CreatePostDto("content", DEFAULT_NEGATIVE_ID, null);

        assertThrows(DataValidationException.class, () -> postService.createDraft((postDto)));
    }

    @Test
    public void testCreateDraftWithNegativeProjectId() {
        CreatePostDto postDto = new CreatePostDto("content", null, DEFAULT_NEGATIVE_ID);

        assertThrows(DataValidationException.class, () -> postService.createDraft((postDto)));
    }

    @Test
    void createDraft_whenAuthorNotFound_shouldThrowEntityNotFoundException() {
        CreatePostDto postDto = new CreatePostDto("content", authorId, null);

        when(userServiceClient.getUser(authorId)).thenThrow(
                new FeignException.NotFound("Not Found",
                        Request.create(Request.HttpMethod.GET, "/users/" + authorId,
                                Map.of(), null, Charset.defaultCharset(),
                                null), null, Map.of())
        );

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postService.createDraft(postDto)
        );

        assertTrue(exception.getMessage().contains("User with ID " + authorId + " not found"));
    }

    @Test
    void createDraft_whenAuthorExists_shouldCreateDraft() {
        CreatePostDto postDto = new CreatePostDto("content", authorId, null);
        when(userServiceClient.getUser(authorId)).thenReturn(ResponseEntity.of(Optional.of(mockUser)));

        Post post = new Post();
        post.setId(1L);
        when(postMapper.toPost(postDto)).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toPostDto(post)).thenReturn(postDtoAuthorExists);

        PostDto result = postService.createDraft(postDto);

        assertNotNull(result);
        verify(userServiceClient).getUser(authorId);
        verify(projectServiceClient, never()).getProject(anyLong());
    }

    @Test
    void createDraft_whenProjectNotFound_shouldThrowEntityNotFoundException() {
        CreatePostDto postDto = new CreatePostDto("content", null, projectId);

        when(projectServiceClient.getProject(projectId)).thenThrow(
                new FeignException.NotFound("Not Found",
                        Request.create(Request.HttpMethod.GET, "/project/" + projectId,
                                Map.of(), null, Charset.defaultCharset(),
                                null), null, Map.of())
        );

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postService.createDraft(postDto)
        );

        assertTrue(exception.getMessage().contains("Project with ID " + projectId + " not found"));
    }

    @Test
    void createDraft_whenProjectExists_shouldCreateDraft() {
        CreatePostDto postDto = new CreatePostDto("content", null, projectId);
        when(projectServiceClient.getProject(projectId)).thenReturn(ResponseEntity.of(Optional.of(mockProject)));

        Post post = new Post();
        post.setId(1L);
        when(postMapper.toPost(postDto)).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toPostDto(post)).thenReturn(postDtoProjectExists);

        PostDto result = postService.createDraft(postDto);

        assertNotNull(result);
        verify(projectServiceClient).getProject(projectId);
        verify(userServiceClient, never()).getUser(anyLong());
    }

    @Test
    void publishPost_whenPostDoesNotExist_shouldThrowEntityNotFoundException() {
        when(postRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.publishPost(DEFAULT_ID));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPost_whenPostAlreadyPublished_shouldThrowForbiddenException() {
        Post post = Post.builder().id(DEFAULT_ID).published(true).build();

        when(postRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(post));

        assertThrows(ForbiddenException.class, () -> postService.publishPost(DEFAULT_ID));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPost_whenPostIsNotPublished_shouldSetPublishedTrue() {
        Post post = Post.builder()
                .id(DEFAULT_ID)
                .published(false)
                .publishedAt(null)
                .build();

        PostDto publishedPostDto = PostDto.builder()
                .id(DEFAULT_ID).content("content").authorId(DEFAULT_ID).projectId(null)
                .published(true).publishedAt(LocalDateTime.now())
                .deleted(false).createdAt(LocalDateTime.now()).updatedAt(null)
                .build();

        doReturn(Optional.of(post)).when(postRepository).findById(DEFAULT_ID);
        doReturn(publishedPostDto).when(postMapper).toPostDto(any(Post.class));
        doReturn(post).when(postRepository).save(any(Post.class));

        PostDto result = postService.publishPost(DEFAULT_ID);

        assertNotNull(result);
        assertTrue(result.published());
        assertNotNull(result.publishedAt());

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();
        assertEquals(DEFAULT_ID, savedPost.getId());
        assertTrue(savedPost.isPublished());
        assertNotNull(savedPost.getPublishedAt());
    }

    @Test
    void updatePost_whenProjectIdNegative_shouldThrowDataValidationException() {
        CreatePostDto dto = new CreatePostDto("content", null, -1L);

        assertThrows(DataValidationException.class, () -> postService.updatePost(postId, dto));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_whenAuthorIdNegative_shouldThrowDataValidationException() {
        CreatePostDto dto = new CreatePostDto("content", -1L, null);

        assertThrows(DataValidationException.class, () -> postService.updatePost(postId, dto));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_whenPostDoesNotExist_shouldThrowEntityNotFoundException() {
        CreatePostDto dto = new CreatePostDto("content", 1L, null);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.updatePost(postId, dto));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_whenProjectIdChanged_shouldThrowDataValidationException() {
        CreatePostDto dto = new CreatePostDto("content", null, 2L); // Changed project ID
        Post existingPost = Post.builder().id(postId).authorId(null).projectId(1L).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        assertThrows(DataValidationException.class, () -> postService.updatePost(postId, dto));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_whenAuthorIdChanged_shouldThrowDataValidationException() {
        CreatePostDto dto = new CreatePostDto("content", 2L, null); // Changed author ID
        Post existingPost = Post.builder().id(postId).authorId(1L).projectId(null).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        assertThrows(DataValidationException.class, () -> postService.updatePost(postId, dto));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_whenPostExistsAndDataMatches_shouldUpdatePost() {
        CreatePostDto dto = new CreatePostDto("new content", DEFAULT_ID, null);

        Post existingPost = Post.builder()
                .id(DEFAULT_ID).authorId(DEFAULT_ID).projectId(null)
                .content("old content").published(false).deleted(false)
                .build();

        PostDto expectedPostDto = PostDto.builder()
                .id(DEFAULT_ID).content("new content").authorId(DEFAULT_ID)
                .projectId(null).published(false).publishedAt(null)
                .deleted(false).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        doReturn(Optional.of(existingPost)).when(postRepository).findById(DEFAULT_ID);
        doReturn(expectedPostDto).when(postMapper).toPostDto(any(Post.class));
        doReturn(existingPost).when(postRepository).save(any(Post.class));

        PostDto result = postService.updatePost(DEFAULT_ID, dto);

        assertNotNull(result);
        assertEquals("new content", existingPost.getContent());
        assertNotNull(existingPost.getUpdatedAt());
        verify(postRepository).save(existingPost);
    }

    @Test
    void softDeletePost_whenPostExists_shouldSetDeletedTrue() {
        Post post = Post.builder()
                .id(postId)
                .published(false)
                .deleted(false)
                .build();
        PostDto dto = PostDto.builder()
                .id(DEFAULT_ID).content("content").authorId(DEFAULT_ID)
                .projectId(null).published(true).publishedAt(LocalDateTime.now())
                .deleted(true).createdAt(LocalDateTime.now()).updatedAt(null)
                .build();

        doReturn(Optional.of(post)).when(postRepository).findById(DEFAULT_ID);
        doReturn(dto).when(postMapper).toPostDto(any(Post.class));
        doReturn(post).when(postRepository).save(any(Post.class));

        PostDto result = postService.softDeletePost(postId);

        assertNotNull(result);
        assertTrue(result.deleted());
        verify(postRepository).save(post);
    }

    @Test
    void getPostById_whenPostExists_shouldReturnPostDto() {
        Post post = Post.builder()
                .id(postId).content("content").authorId(DEFAULT_ID)
                .projectId(null).published(false).deleted(false)
                .build();
        PostDto dto = PostDto.builder()
                .id(postId).content("content").authorId(DEFAULT_ID)
                .projectId(null).published(false).deleted(false)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toPostDto(post)).thenReturn(dto);

        PostDto result = postService.getPostById(postId);

        assertNotNull(result);
        assertEquals(postId, result.id());
        assertEquals("content", result.content());
    }

    @Test
    void getPostById_whenPostDoesNotExist_shouldThrowEntityNotFoundException() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(postId));
    }

    @Test
    void getDraftsByUser_whenUserHasDrafts_shouldReturnSortedDrafts() {
        Post draft1 = Post.builder()
                .id(1L).authorId(userId).published(false)
                .deleted(false).createdAt(time1)
                .build();
        Post draft2 = Post.builder()
                .id(2L).authorId(userId).published(false)
                .deleted(false).createdAt(time2)
                .build();

        when(postRepository.findByAuthorId(userId)).thenReturn(List.of(draft1, draft2));

        List<PostDto> expected = List.of(postMapper.toPostDto(draft1), postMapper.toPostDto(draft2));

        when(postMapper.toPostDto(draft1)).thenReturn(expected.get(0));
        when(postMapper.toPostDto(draft2)).thenReturn(expected.get(1));

        List<PostDto> result = postService.getDraftsByUser(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expected.get(0), result.get(0));
        assertEquals(expected.get(1), result.get(1));
    }

    @Test
    void getDraftsByUser_whenNoDraftsExist_shouldReturnEmptyList() {
        when(postRepository.findByAuthorId(userId)).thenReturn(Collections.emptyList());

        List<PostDto> result = postService.getDraftsByUser(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postMapper, never()).toPostDto(any(Post.class));
    }

    @Test
    void getDraftsByProject_whenProjectHasDrafts_shouldReturnSortedDrafts() {
        Post draft1 = Post.builder()
                .id(1L).projectId(projectId).published(false)
                .deleted(false).createdAt(time1)
                .build();
        Post draft2 = Post.builder()
                .id(2L).projectId(projectId).published(false)
                .deleted(false).createdAt(time2)
                .build();

        when(postRepository.findByProjectId(projectId)).thenReturn(List.of(draft1, draft2));

        List<PostDto> expected = List.of(postMapper.toPostDto(draft1), postMapper.toPostDto(draft2));

        when(postMapper.toPostDto(draft1)).thenReturn(expected.get(0));
        when(postMapper.toPostDto(draft2)).thenReturn(expected.get(1));

        List<PostDto> result = postService.getDraftsByProject(projectId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expected.get(0), result.get(0));
        assertEquals(expected.get(1), result.get(1));
    }

    @Test
    void getPublishedByUser_whenUserHasPublishedPosts_shouldReturnSortedPublishedPosts() {
        Post published1 = Post.builder()
                .id(1L).authorId(userId).published(true)
                .deleted(false).publishedAt(time1)
                .build();
        Post published2 = Post.builder()
                .id(2L).authorId(userId).published(true)
                .deleted(false).publishedAt(time2)
                .build();

        when(postRepository.findByAuthorId(userId)).thenReturn(List.of(published1, published2));

        List<PostDto> expected = List.of(postMapper.toPostDto(published1), postMapper.toPostDto(published2));

        when(postMapper.toPostDto(published1)).thenReturn(expected.get(0));
        when(postMapper.toPostDto(published2)).thenReturn(expected.get(1));

        List<PostDto> result = postService.getPublishedByUser(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expected.get(0), result.get(0));
        assertEquals(expected.get(1), result.get(1));
    }

    @Test
    void getPublishedByProject_whenProjectHasPublishedPosts_shouldReturnSortedPublishedPosts() {
        Post published1 = Post.builder()
                .id(1L).projectId(projectId).published(true)
                .deleted(false).publishedAt(time1)
                .build();
        Post published2 = Post.builder()
                .id(2L).projectId(projectId).published(true)
                .deleted(false).publishedAt(time2)
                .build();

        when(postRepository.findByProjectId(projectId)).thenReturn(List.of(published1, published2));

        List<PostDto> expected = List.of(postMapper.toPostDto(published1), postMapper.toPostDto(published2));

        when(postMapper.toPostDto(published1)).thenReturn(expected.get(0));
        when(postMapper.toPostDto(published2)).thenReturn(expected.get(1));

        List<PostDto> result = postService.getPublishedByProject(projectId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expected.get(0), result.get(0));
        assertEquals(expected.get(1), result.get(1));
    }

    @Test
    void publishScheduledPosts_DoesNothingWhenNoPosts() {
        when(postRepository.findReadyToPublish()).thenReturn(Collections.emptyList());

        postService.publishScheduledPosts();

        verify(postRepository, never()).saveAll(anyList());
        verify(scheduledPostExecutor, never()).execute(any(Runnable.class));
    }

    @Test
    void publishScheduledPosts_CallsSaveAllWithPosts() {
        Post post1 = createTestPost(1L, "Content 1");
        Post post2 = createTestPost(2L, "Content 2");
        List<Post> posts = List.of(post1, post2);

        when(postRepository.findReadyToPublish()).thenReturn(posts);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(scheduledPostExecutor).execute(any(Runnable.class));

        postService.publishScheduledPosts();

        verify(postRepository, atLeastOnce()).saveAll(anyList());
        assertTrue(post1.isPublished());
        assertNotNull(post1.getPublishedAt());
        assertTrue(post2.isPublished());
        assertNotNull(post2.getPublishedAt());
    }

    @Test
    void publishScheduledPosts_WithMultipleBatches_PublishesAllBatches() throws Exception {
        // Arrange - устанавливаем batchSize = 1 для теста множественных батчей
        Field batchSizeField = PostServiceImpl.class.getDeclaredField("batchSize");
        batchSizeField.setAccessible(true);
        batchSizeField.set(postService, 1);

        Post post1 = createTestPost(1L, "Content 1");
        Post post2 = createTestPost(2L, "Content 2");
        Post post3 = createTestPost(3L, "Content 3");
        List<Post> posts = List.of(post1, post2, post3);

        when(postRepository.findReadyToPublish()).thenReturn(posts);

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(scheduledPostExecutor).execute(any(Runnable.class));

        postService.publishScheduledPosts();

        verify(postRepository, times(3)).saveAll(anyList());
    }

    @Test
    void partitionList_WithEmptyList_ReturnsEmptyList() {
        List<List<Post>> result = postService.partitionList(Collections.emptyList(), 2);

        assertTrue(result.isEmpty());
    }

    @Test
    void partitionList_WithZeroBatchSize_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> postService.partitionList(List.of("test"), 0));
    }

    @Test
    void publishScheduledPosts_WhenBatchFails_LogsErrorButContinues() {
        Post post1 = createTestPost(1L, "Content 1");
        Post post2 = createTestPost(2L, "Content 2");
        List<Post> posts = List.of(post1, post2);

        when(postRepository.findReadyToPublish()).thenReturn(posts);

        ReflectionTestUtils.setField(postService, "batchSize", 1);

        when(postRepository.saveAll(anyList()))
                .thenThrow(new RuntimeException("DB error"))
                .thenReturn(List.of(post2));

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(scheduledPostExecutor).execute(any(Runnable.class));

        assertDoesNotThrow(() -> postService.publishScheduledPosts());

        verify(postRepository, times(2)).saveAll(anyList());
    }

    private Post createTestPost(Long id, String content) {
        return Post.builder()
                .id(id)
                .content(content)
                .authorId(1L)
                .published(false)
                .deleted(false)
                .scheduledAt(LocalDateTime.now().minusHours(1))
                .build();
    }
}
    void testFindAuthorsForBanNotCallPublishIfPostsLessThanMax() {
        List<Post> posts = new ArrayList<>();

        for (int i = 0; i < maxUnverifiedPosts; i++) {
            posts.add(Post.builder().authorId(1L).build());
        }

        Page<Post> firstPage = new PageImpl<>(posts);
        Page<Post> emptyPage = Page.empty();
        when(postRepository.findUnverified(any(Pageable.class)))
                .thenReturn(firstPage)
                .thenReturn(emptyPage);

        postService.findAuthorsForBan();

        verify(userServiceClient, Mockito.never()).getUsersByIds(Mockito.any(GetUsersDto.class));
        verify(userBanEventPublisher, Mockito.never()).publish(Mockito.any(UserBanEvent.class));
    }

    @Test
    void testFindAuthorsForBanPositive() {
        List<Post> posts = new ArrayList<>();
        long userId = 1L;
        for (int i = 0; i < maxUnverifiedPosts + 1; i++) {
            posts.add(Post.builder().authorId(userId).build());
        }

        List<Long> usersIds = List.of(userId);
        Page<Post> firstPage = new PageImpl<>(posts);
        Page<Post> emptyPage = Page.empty();

        when(postRepository.findUnverified(any(Pageable.class)))
                .thenReturn(firstPage)
                .thenReturn(emptyPage);
        when(userService.getNotBannedUsersIds(Mockito.anyList())).thenReturn(usersIds);

        postService.findAuthorsForBan();

        ArgumentCaptor<List<Long>> longListArgumentCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<UserBanEvent> userBanEventArgumentCaptor = ArgumentCaptor.forClass(UserBanEvent.class);

        verify(userService).getNotBannedUsersIds(longListArgumentCaptor.capture());
        verify(userBanEventPublisher).publish(userBanEventArgumentCaptor.capture());

        List<Long> allAuthorsIds = longListArgumentCaptor.getValue();
        List<Long> usersForBan = userBanEventArgumentCaptor.getValue().userIds();

        assertEquals(1, allAuthorsIds.size());
        assertEquals(1, usersForBan.size());
        assertEquals(userId, allAuthorsIds.get(0));
        assertEquals(userId, usersForBan.get(0));
    }
}

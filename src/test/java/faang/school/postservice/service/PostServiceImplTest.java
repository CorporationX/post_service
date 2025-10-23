package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
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
    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    private static final long POST_ID = 1L;
    private static final long AUTHOR_ID = 1L;
    private static final long PROJECT_ID = 1L;

    private final PostDto postDtoAuthorExists = PostDto.builder().id(POST_ID).content("content")
            .authorId(AUTHOR_ID).projectId(null)
            .published(false).publishedAt(null).deleted(false)
            .createdAt(LocalDateTime.now()).updatedAt(null)
            .build();

    private final PostDto postDtoProjectExists = PostDto.builder().id(POST_ID).content("content")
            .authorId(null).projectId(PROJECT_ID)
            .published(false).publishedAt(null).deleted(false)
            .createdAt(LocalDateTime.now()).updatedAt(null)
            .build();

    private final UserDto mockUser = new UserDto(1L, "mockUser", "mockUser@example.com");
    private final ProjectDto mockProject = new ProjectDto(1L, "mockProject");

    @Test
    public void testCreateDraftWithoutAuthorAndProject() {
        CreatePostDto postDto = new CreatePostDto("content", null, null);

        assertThrows(DataValidationException.class, () -> postService.createDraft(postDto));
    }

    @Test
    public void testCreateDraftWithBothAuthorAndProject() {
        CreatePostDto postDto = new CreatePostDto("content", 1L, 1L);

        assertThrows(DataValidationException.class, () -> postService.createDraft((postDto)));
    }

    @Test
    public void testCreateDraftWithNegativeAuthorId() {
        CreatePostDto postDto = new CreatePostDto("content", -1L, null);

        assertThrows(DataValidationException.class, () -> postService.createDraft((postDto)));
    }

    @Test
    public void testCreateDraftWithNegativeProjectId() {
        CreatePostDto postDto = new CreatePostDto("content", null, -1L);

        assertThrows(DataValidationException.class, () -> postService.createDraft((postDto)));
    }

    @Test
    void createDraft_whenAuthorNotFound_shouldThrowEntityNotFoundException() {
        long authorId = AUTHOR_ID;
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
        long authorId = AUTHOR_ID;
        CreatePostDto postDto = new CreatePostDto("content", authorId, null);
        when(userServiceClient.getUser(authorId)).thenReturn(mockUser);

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
        long projectId = PROJECT_ID;
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
        long projectId = PROJECT_ID;
        CreatePostDto postDto = new CreatePostDto("content", null, projectId);
        when(projectServiceClient.getProject(projectId)).thenReturn(mockProject);

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
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.publishPost(POST_ID));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPost_whenPostAlreadyPublished_shouldThrowForbiddenException() {
        Post post = Post.builder().id(POST_ID).published(true).build();

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        assertThrows(ForbiddenException.class, () -> postService.publishPost(POST_ID));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPost_whenPostIsNotPublished_shouldSetPublishedTrue() {
        Post post = Post.builder()
                .id(POST_ID)
                .published(false)
                .publishedAt(null)
                .build();

        PostDto publishedPostDto = PostDto.builder()
                .id(POST_ID).content("content").authorId(AUTHOR_ID).projectId(null)
                .published(true).publishedAt(LocalDateTime.now())
                .deleted(false).createdAt(LocalDateTime.now()).updatedAt(null)
                .build();

        doReturn(Optional.of(post)).when(postRepository).findById(POST_ID);
        doReturn(publishedPostDto).when(postMapper).toPostDto(any(Post.class));
        doReturn(post).when(postRepository).save(any(Post.class));

        PostDto result = postService.publishPost(POST_ID);

        assertNotNull(result);
        assertTrue(result.published());
        assertNotNull(result.publishedAt());

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());

        Post savedPost = postCaptor.getValue();
        assertEquals(POST_ID, savedPost.getId());
        assertTrue(savedPost.isPublished());
        assertNotNull(savedPost.getPublishedAt());
    }

    @Test
    void updatePost_whenProjectIdNegative_shouldThrowDataValidationException() {
        long postId = 1L;
        CreatePostDto dto = new CreatePostDto("content", null, -1L);

        assertThrows(DataValidationException.class, () -> postService.updatePost(postId, dto));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_whenAuthorIdNegative_shouldThrowDataValidationException() {
        long postId = 1L;
        CreatePostDto dto = new CreatePostDto("content", -1L, null);

        assertThrows(DataValidationException.class, () -> postService.updatePost(postId, dto));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_whenPostDoesNotExist_shouldThrowEntityNotFoundException() {
        long postId = 1L;
        CreatePostDto dto = new CreatePostDto("content", 1L, null);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.updatePost(postId, dto));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_whenProjectIdChanged_shouldThrowDataValidationException() {
        Long postId = 1L;
        CreatePostDto dto = new CreatePostDto("content", null, 2L); // Changed project ID
        Post existingPost = Post.builder().id(postId).authorId(null).projectId(1L).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        assertThrows(DataValidationException.class, () -> postService.updatePost(postId, dto));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_whenAuthorIdChanged_shouldThrowDataValidationException() {
        long postId = 1L;
        CreatePostDto dto = new CreatePostDto("content", 2L, null); // Changed author ID
        Post existingPost = Post.builder().id(postId).authorId(1L).projectId(null).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        assertThrows(DataValidationException.class, () -> postService.updatePost(postId, dto));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void updatePost_whenPostExistsAndDataMatches_shouldUpdatePost() {
        CreatePostDto dto = new CreatePostDto("new content", AUTHOR_ID, null);

        Post existingPost = Post.builder()
                .id(POST_ID).authorId(AUTHOR_ID).projectId(null)
                .content("old content").published(false).deleted(false)
                .build();

        PostDto expectedPostDto = PostDto.builder()
                .id(POST_ID).content("new content").authorId(AUTHOR_ID)
                .projectId(null).published(false).publishedAt(null)
                .deleted(false).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        doReturn(Optional.of(existingPost)).when(postRepository).findById(POST_ID);
        doReturn(expectedPostDto).when(postMapper).toPostDto(any(Post.class));
        doReturn(existingPost).when(postRepository).save(any(Post.class));

        PostDto result = postService.updatePost(POST_ID, dto);

        assertNotNull(result);
        assertEquals("new content", existingPost.getContent());
        assertNotNull(existingPost.getUpdatedAt());
        verify(postRepository).save(existingPost);
    }

    @Test
    void softDeletePost_whenPostExists_shouldSetDeletedTrue() {
        long postId = POST_ID;
        Post post = Post.builder()
                .id(postId)
                .published(false)
                .deleted(false)
                .build();
        PostDto dto = PostDto.builder()
                .id(POST_ID).content("content").authorId(AUTHOR_ID)
                .projectId(null).published(true).publishedAt(LocalDateTime.now())
                .deleted(true).createdAt(LocalDateTime.now()).updatedAt(null)
                .build();

        doReturn(Optional.of(post)).when(postRepository).findById(POST_ID);
        doReturn(dto).when(postMapper).toPostDto(any(Post.class));
        doReturn(post).when(postRepository).save(any(Post.class));

        PostDto result = postService.softDeletePost(postId);

        assertNotNull(result);
        assertTrue(result.deleted());
        verify(postRepository).save(post);
    }

    @Test
    void getPostById_whenPostExists_shouldReturnPostDto() {
        long postId = POST_ID;
        Post post = Post.builder()
                .id(postId).content("content").authorId(AUTHOR_ID)
                .projectId(null).published(false).deleted(false)
                .build();
        PostDto dto = PostDto.builder()
                .id(postId).content("content").authorId(AUTHOR_ID)
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
        long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(postId));
    }

    @Test
    void getDraftsByUser_whenUserHasDrafts_shouldReturnSortedDrafts() {
        long userId = AUTHOR_ID;
        Post draft1 = Post.builder()
                .id(1L).authorId(userId).published(false)
                .deleted(false).createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
        Post draft2 = Post.builder()
                .id(2L).authorId(userId).published(false)
                .deleted(false).createdAt(LocalDateTime.of(2024, 1, 2, 0, 0))
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
        long userId = AUTHOR_ID;

        when(postRepository.findByAuthorId(userId)).thenReturn(Collections.emptyList());

        List<PostDto> result = postService.getDraftsByUser(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(postMapper, never()).toPostDto(any(Post.class));
    }

    @Test
    void getDraftsByProject_whenProjectHasDrafts_shouldReturnSortedDrafts() {
        Long projectId = PROJECT_ID;
        Post draft1 = Post.builder()
                .id(1L).projectId(projectId).published(false)
                .deleted(false).createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
        Post draft2 = Post.builder()
                .id(2L).projectId(projectId).published(false)
                .deleted(false).createdAt(LocalDateTime.of(2024, 1, 2, 0, 0))
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
        long userId = AUTHOR_ID;
        Post published1 = Post.builder()
                .id(1L).authorId(userId).published(true)
                .deleted(false).publishedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
        Post published2 = Post.builder()
                .id(2L).authorId(userId).published(true)
                .deleted(false).publishedAt(LocalDateTime.of(2024, 1, 2, 0, 0))
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
        long projectId = PROJECT_ID;
        Post published1 = Post.builder()
                .id(1L).projectId(projectId).published(true)
                .deleted(false).publishedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
        Post published2 = Post.builder()
                .id(2L).projectId(projectId).published(true)
                .deleted(false).publishedAt(LocalDateTime.of(2024, 1, 2, 0, 0))
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


}
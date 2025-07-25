package faang.school.postservice.controller;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PostControllerTest {

    @Mock
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @InjectMocks
    private PostController postController;

   /*@Test
    public void testCreateDraftPost() {
        PostRequestDto request = createRequest("Post", 2L, null);
        PostResponseDto postDto = createPostResponseDto(request);

        when(postService.createDraftPost(request))
                .thenReturn(postDto);

        when(userServiceClient.getUser(postDto.authorId()))
                .thenReturn(new UserDto(1L, "Miras",
                        "mirasospan62@gmail.com"));

        PostResponseDto result = postController.createDraftPost(request);

        verify(postService, Mockito.times(1))
                .createDraftPost(request);
        assertEquals("Post", result.content());
    }

    @Test
    public void testCreateDraftPostWithNullContent() {
        PostRequestDto request = createRequest(null, 2L, null);

        when(postService.createDraftPost(request))
                .thenThrow(new IllegalArgumentException("Post cannot be empty!"));

        assertThrows(IllegalArgumentException.class,
                () -> postService.createDraftPost(request));
    }

    @Test
    public void testCreateDraftPostWithTwoOwner() {
        PostRequestDto request = createRequest("Post", 2L, 2L);

        when(postService.createDraftPost(request))
                .thenThrow(new IllegalArgumentException("Post cannot have more than 1 owner!"));

        assertThrows(IllegalArgumentException.class,
                () -> postService.createDraftPost(request));
    }

    @Test
    public void testCreateDraftPostWithNullUser() {
        PostRequestDto request = createRequest("Post", 2L, null);

        when(postService.createDraftPost(request))
                .thenThrow(new EntityNotFoundException("User with id: " + request.authorId() + " not found!"));

        assertThrows(EntityNotFoundException.class,
                () -> postService.createDraftPost(request));
    }

    @Test
    public void testCreateDraftPostWithNullProject() {
        PostRequestDto request = createRequest("Post", null, 2L);

        when(postService.createDraftPost(request))
                .thenThrow(new EntityNotFoundException("Project with id: " + request.projectId() + " not found!"));

        assertThrows(EntityNotFoundException.class,
                () -> postService.createDraftPost(request));
    }

    @Test
    public void testPublishPost() {
        PostRequestDto request = createRequest("Post", 2L, null);
        PostResponseDto postDto = createPostResponseDto(request);
        long postId = postDto.id();

        when(postService.publishPost(postId))
                .thenReturn(postDto);

        PostResponseDto result = postController.publishPost(postId);

        assertEquals(postDto, result);
    }

    @Test
    public void testUpdatePost() {
        PostRequestDto request = createRequest("Post", null, 2L);
        PostResponseDto postDto = createPostResponseDto(request);
        long postId = postDto.id();

        when(postService.updatePost(postId, request))
                .thenReturn(postDto);

        when(projectServiceClient.getProject(postDto.projectId()))
                .thenReturn(new ProjectDto(1L, "Title"));

        PostResponseDto result = postController.updatePost(postId, request);

        assertEquals(postDto, result);
    }

    @Test
    public void testDeletePost() {
        PostResponseDto postDto = new PostResponseDto(1L, "Post", 2L, null,
                false, null,
                null, null, true);
        long postId = postDto.id();

        when(postService.deletePost(postId))
                .thenReturn(postDto);

        postController.deletePost(postId);

        assertTrue(postDto.deleted());
    }

    @Test
    public void testGetPostResponseDtoById() {
        PostRequestDto request = createRequest("Post", null, 2L);
        PostResponseDto postDto = createPostResponseDto(request);
        long postId = postDto.id();

        when(postService.getPostById(postId))
                .thenReturn(postDto);

        PostResponseDto result = postController.getPostById(postId);

        assertNotNull(result);
        assertEquals(postId, result.id());
    }

    @Test
    public void testGetAllNotDeletedDraftsByAuthorId() {
        PostRequestDto request = createRequest("Post", 2L, null);
        PostResponseDto draft = createPostResponseDto(request);
        long draftAuthorId = draft.authorId();
        List<PostResponseDto> drafts = List.of(draft);

        when(postService.getAllNotDeletedDraftsByAuthorId(draftAuthorId))
                .thenReturn(drafts);

        List<PostResponseDto> result = postController.getAllNotDeletedDraftsByAuthorId(draftAuthorId);

        assertNotNull(result);
        assertEquals(drafts, result);
    }

    @Test
    public void testGetAllNotDeletedDraftsByProjectId() {
        PostRequestDto request = createRequest("Post", null, 2L);
        PostResponseDto draft = createPostResponseDto(request);
        long draftProjectId = draft.projectId();
        List<PostResponseDto> drafts = List.of(draft);

        when(postService.getAllNotDeletedDraftsByProjectId(draftProjectId))
                .thenReturn(drafts);

        List<PostResponseDto> result = postController.getAllNotDeletedDraftsByProjectId(draftProjectId);

        assertNotNull(result);
        assertEquals(drafts, result);
    }

    @Test
    public void testGetAllNotDeletedPostsByAuthorId() {
        PostRequestDto request = createRequest("Post", 2L, null);
        PostResponseDto post = createPostResponseDto(request);
        long postAuthorId = post.authorId();
        List<PostResponseDto> posts = List.of(post);

        when(postService.getAllPostsByAuthorId(postAuthorId))
                .thenReturn(posts);

        List<PostResponseDto> result = postController.getAllNotDeletedPostsByAuthorId(postAuthorId);

        assertNotNull(result);
        assertEquals(posts, result);
    }

    @Test
    public void testGetAllNotDeletedPostsByProjectId() {
        PostRequestDto request = createRequest("Post", null, 2L);
        PostResponseDto post = createPostResponseDto(request);
        long postProjectId = post.projectId();
        List<PostResponseDto> posts = List.of(post);

        when(postService.getAllPostsByProjectId(postProjectId))
                .thenReturn(posts);

        List<PostResponseDto> result = postController.getAllNotDeletedPostsByProjectId(postProjectId);

        assertNotNull(result);
        assertEquals(posts, result);
    }

    private PostResponseDto createPostResponseDto(PostRequestDto request) {
        return new PostResponseDto(
                1L, request.content(), request.authorId(), request.projectId(),
                false, null,
                null, null, false);
    }

    private PostRequestDto createRequest(String content, Long authorId, Long projectId) {
        return new PostRequestDto(content, authorId, projectId);
    }*/
}
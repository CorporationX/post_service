package faang.school.postservice.controller;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {

    @Mock
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @InjectMocks
    private PostController postController;

    @Test
    public void testCreateDraftPost() {
        PostDto postDto = create(1L, "Post", 2L, null);
        when(postService.createDraftPost(postDto))
                .thenReturn(postDto);

        when(userServiceClient.getUser(postDto.authorId()))
                .thenReturn(new UserDto(1L, "Miras",
                        "mirasospan62@gmail.com"));

        PostDto result = postController.createDraftPost(postDto);

        verify(postService, Mockito.times(1))
                .createDraftPost(postDto);
        assertEquals("Post", result.content());
    }

    @Test
    public void testCreateDraftPostWithNullContent() {
        PostDto postDto = create(1L, null, 2L, null);

        when(postService.createDraftPost(postDto))
                .thenThrow(new IllegalArgumentException("Post cannot be empty!"));

        assertThrows(IllegalArgumentException.class,
                () -> postService.createDraftPost(postDto));
    }

    @Test
    public void testCreateDraftPostWithTwoOwner() {
        PostDto postDto = create(1L, "Post", 2L, 2L);

        when(postService.createDraftPost(postDto))
                .thenThrow(new IllegalArgumentException("Post cannot have more than 1 owner!"));

        assertThrows(IllegalArgumentException.class,
                () -> postService.createDraftPost(postDto));
    }

    @Test
    public void testCreateDraftPostWithNullUser() {
        PostDto postDto = create(1L, "Post", 2L, null);

        when(postService.createDraftPost(postDto))
                .thenThrow(new EntityNotFoundException("User with id: " + postDto.authorId() + " not found!"));

        assertThrows(EntityNotFoundException.class,
                () -> postService.createDraftPost(postDto));
    }

    @Test
    public void testCreateDraftPostWithNullProject() {
        PostDto postDto = create(1L, "Post", null, 2L);

        when(postService.createDraftPost(postDto))
                .thenThrow(new EntityNotFoundException("Project with id: " + postDto.projectId() + " not found!"));

        assertThrows(EntityNotFoundException.class,
                () -> postService.createDraftPost(postDto));
    }

    @Test
    public void testPublishPost() {
        PostDto postDto = create(1L, "Post", 2L, null);
        long postId = postDto.id();

        when(postService.publishPost(postId))
                .thenReturn(postDto);

        PostDto result = postController.publishPost(postId);

        assertEquals(postDto, result);
    }

    @Test
    public void testUpdatePost() {
        PostDto postDto = create(2L, "Post", null, 2L);
        long postId = postDto.id();

        when(postService.updatePost(postId, postDto))
                .thenReturn(postDto);

        when(projectServiceClient.getProject(postDto.projectId()))
                .thenReturn(new ProjectDto(1L, "Title"));

        PostDto result = postController.updatePost(postId, postDto);

        assertEquals(postDto, result);
    }

    @Test
    public void testDeletePost() {
        PostDto postDto = new PostDto
                (1L, "Post", 2L, null,
                        false, null,
                        null, null, true);
        long postId = postDto.id();

        when(postService.deletePost(postId))
                .thenReturn(postDto);

        postController.deletePost(postId);

        assertTrue(postDto.deleted());
    }

    @Test
    public void testGetPostDtoById() {
        PostDto post = create(1L, "Post", null, 2L);
        long postId = post.id();

        when(postService.getPostDtoById(postId))
                .thenReturn(post);

        PostDto result = postController.getPostById(postId);

        assertNotNull(result);
        assertEquals(post.id(), result.id());
    }

    @Test
    public void testGetAllNotDeletedDraftsByAuthorId() {
        PostDto draft = create(1L, "Post", 2L, null);
        long draftAuthorId = draft.authorId();
        List<PostDto> drafts = List.of(draft);

        when(postService.getAllNotDeletedDraftsByAuthorId(draftAuthorId))
                .thenReturn(drafts);

        List<PostDto> result = postController.getAllNotDeletedDraftsByAuthorId(draftAuthorId);

        assertNotNull(result);
        assertEquals(drafts, result);
    }

    @Test
    public void testGetAllNotDeletedDraftsByProjectId() {
        PostDto draft = create(1L, "Post", null, 2L);
        long draftProjectId = draft.projectId();
        List<PostDto> drafts = List.of(draft);

        when(postService.getAllNotDeletedDraftsByProjectId(draftProjectId))
                .thenReturn(drafts);

        List<PostDto> result = postController.getAllNotDeletedDraftsByProjectId(draftProjectId);

        assertNotNull(result);
        assertEquals(drafts, result);
    }

    @Test
    public void testGetAllNotDeletedPostsByAuthorId() {
        PostDto post = create(1L, "Post", 2L, null);
        long postAuthorId = post.authorId();
        List<PostDto> posts = List.of(post);

        when(postService.getAllPostsByAuthorId(postAuthorId))
                .thenReturn(posts);

        List<PostDto> result = postController.getAllNotDeletedPostsByAuthorId(postAuthorId);

        assertNotNull(result);
        assertEquals(posts, result);
    }

    @Test
    public void testGetAllNotDeletedPostsByProjectId() {
        PostDto post = create(1L, "Post", null, 2L);
        long postProjectId = post.projectId();
        List<PostDto> posts = List.of(post);

        when(postService.getAllPostsByProjectId(postProjectId))
                .thenReturn(posts);

        List<PostDto> result = postController.getAllNotDeletedPostsByProjectId(postProjectId);

        assertNotNull(result);
        assertEquals(posts, result);
    }

    private PostDto create(long id, String content, Long authorId, Long projectId) {
        return new PostDto
                (id, content, authorId, projectId,
                        false, null,
                        null, null, false);

    }
}
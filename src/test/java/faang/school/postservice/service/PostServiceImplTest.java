package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostCreateEventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.ContactDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.AuthorNotFoundException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.post.RedisPostCreateEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostActionService;
import faang.school.postservice.service.post.PostServiceImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private UserContext userContext;
    @Mock
    private PostActionService postInteractionService;
    @Mock
    private RedisPostCreateEventPublisher redisPostCreateEventPublisher;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void testCreateDraft_whenValidUserProvided_thenReturnsSavedPost() {
        PostDto dto = new PostDto(null, "valid content", 1L, null);
        Post post = new Post();
        Post saved = new Post();
        saved.setId(100L);

        when(userServiceClient.getUser(1L)).thenReturn(new UserDto(1L, "name", "email", "EMAIL", new ArrayList<ContactDto>()));
        when(postMapper.toEntity(dto)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(saved);
        when(postMapper.toDto(saved)).thenReturn(new PostDto(100L, "valid content", 1L, null));

        PostDto result = postService.createDraft(dto);

        assertNotNull(result);
        assertEquals(100L, result.id());
    }

    @Test
    void testCreateDraft_whenAuthorMissing_thenThrowsException() {
        PostDto dto = new PostDto(null, "content", null, null);
        assertThrows(DataValidationException.class, () -> postService.createDraft(dto));
    }

    @Test
    void testCreateDraft_whenBothAuthors_thenThrowsException() {
        PostDto dto = new PostDto(null, "content", 1L, 2L);
        assertThrows(DataValidationException.class, () -> postService.createDraft(dto));
    }

    @Test
    void testValidateAuthor_whenUserNotFound_thenThrowsException() {
        PostDto dto = new PostDto(null, "text", 99L, null);
        when(userServiceClient.getUser(99L))
                .thenThrow(new AuthorNotFoundException("User not found"));
        assertThrows(AuthorNotFoundException.class, () -> postService.createDraft(dto));
    }

    @Test
    void testValidateAuthor_whenProjectNotFound_thenThrowsException() {
        PostDto dto = new PostDto(null, "text", null, 77L);
        when(projectServiceClient.getProject(77L))
                .thenThrow(new AuthorNotFoundException("Project not found"));
        assertThrows(AuthorNotFoundException.class, () -> postService.createDraft(dto));
    }

    @Test
    void testPublishPost_whenAlreadyPublished_thenThrowsException() {
        Post post = new Post();
        post.setPublished(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> postService.publishPost(1L));
    }

    @Test
    @Disabled
    void testPublishPost_whenNotYetPublished_thenSetsPublishedAndReturnsDto() {
        Post post = new Post();
        post.setPublished(false);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(new PostDto(1L, "published", 1L, null));

        PostDto result = postService.publishPost(1L);

        assertTrue(post.isPublished());
        assertNotNull(post.getPublishedAt());
        assertEquals("published", result.content());
    }

    @Test
    void testUpdatePost_whenAuthorChanged_thenThrowsException() {
        Post post = new Post();
        post.setAuthorId(1L);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        PostDto dto = new PostDto(1L, "update", 2L, null);

        assertThrows(DataValidationException.class, () -> postService.updatePost(1L, dto));
    }

    @Test
    void testUpdatePost_whenAuthorSame_thenUpdatesContentAndReturnsDto() {
        Post post = new Post();
        post.setAuthorId(1L);
        PostDto dto = new PostDto(1L, "updated", 1L, null);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(dto);

        PostDto result = postService.updatePost(1L, dto);

        assertEquals("updated", result.content());
    }

    @Test
    void testDeletePost_whenCalled_thenMarksPostAsDeleted() {
        Post post = new Post();
        post.setDeleted(false);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L);

        assertTrue(post.isDeleted());
        verify(postRepository).save(post);
    }

    @Test
    void testGetPost_whenNotFound_thenThrowsException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class, () -> postService.getPost(1L));
    }

    @Test
    void testGetAllDraftsByAuthorId_whenDraftsExist_thenReturnsMappedList() {
        Post post = new Post();
        when(postRepository.findDraftsByAuthor(1L)).thenReturn(List.of(post));
        when(postMapper.toDto(post)).thenReturn(new PostDto(1L, "text", 1L, null));

        List<PostDto> result = postService.getAllDraftsByAuthorId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void testGetAllPostsByProjectId_whenPostsExist_thenReturnsMappedList() {
        Post post = new Post();
        when(postRepository.findPublishedByProject(3L)).thenReturn(List.of(post));
        when(postMapper.toDto(post)).thenReturn(new PostDto(1L, "text", null, 3L));

        List<PostDto> result = postService.getAllPostsByProjectId(3L);

        assertEquals(1, result.size());
    }
}

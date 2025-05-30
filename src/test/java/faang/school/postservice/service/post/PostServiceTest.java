package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.client.RemoteNotFoundException;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.validation.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostValidator postValidator;
    @Mock
    private UserContext userContext;
    @Captor
    private ArgumentCaptor<Post> postCaptor;
    @InjectMocks
    private PostService postService;

    private Post post;

    @BeforeEach
    public void setUp() {
        post = new Post();
        post.setId(5L);
        post.setContent("Test content");
    }

    @Test
    public void getPostById_postFound() {
        when(postRepository.findById(eq(post.getId()))).thenReturn(Optional.of(post));

        Post returnPost = postService.getPostById(post.getId());

        assertEquals(returnPost, post);
        verify(postRepository, times(1)).findById(eq(post.getId()));
    }

    @Test
    public void getPostById_postNotFound() {
        when(postRepository.findById(eq(post.getId()))).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.getPostById(post.getId()));
        verify(postRepository, times(1)).findById(eq(post.getId()));
    }

    @Test
    public void testCreateDraftPost_successfully() {
        when(postRepository.save(eq(post))).thenReturn(post);

        Post returnPost = postService.createDraftPost(post);

        assertEquals(returnPost, post);
        verify(postRepository, times(1)).save(eq(post));
    }

    @Test
    public void testCreateDraftPost_authorOrProjectNotFound() {
        doThrow(RemoteNotFoundException.class)
                .when(postValidator)
                .checkPost(post);

        assertThrows(RemoteNotFoundException.class, () -> postService.createDraftPost(post));
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testPublishPost_successfully() {
        when(postRepository.findById(eq(post.getId()))).thenReturn(Optional.of(post));
        when(postRepository.save(eq(post))).thenReturn(post);

        Post returnPost = postService.publishPost(post.getId());

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();
        assertEquals(returnPost, post);
        assertEquals(savedPost, post);
        assertTrue(savedPost.isPublished());
        assertNotNull(savedPost.getPublishedAt());
        verify(postRepository, times(1)).save(eq(post));
    }

    @Test
    public void testPublishPost_postNotFound() {
        when(postRepository.findById(eq(post.getId()))).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.publishPost(post.getId()));
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testPublishPost_postAlreadyPublished() {
        when(postRepository.findById(eq(post.getId()))).thenReturn(Optional.of(post));
        doThrow(PostAlreadyPublishedException.class)
                .when(postValidator)
                .checkPostIsNotPublished(post);

        assertThrows(PostAlreadyPublishedException.class, () -> postService.publishPost(post.getId()));
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testUpdatePost() {
        when(postRepository.save(eq(post))).thenReturn(post);

        Post returnPost = postService.updatePost(post);

        assertEquals(returnPost, post);
        verify(postRepository, times(1)).save(eq(post));
    }

    @Test
    public void testDeletePost_successfully() {
        when(postRepository.findById(eq(post.getId()))).thenReturn(Optional.of(post));
        when(postRepository.save(eq(post))).thenReturn(post);

        postService.deletePost(post.getId());

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();
        assertEquals(savedPost, post);
        assertTrue(savedPost.isDeleted());
        assertNotNull(savedPost.getDeletedAt());
    }

    @Test
    public void testDeletePost_postNotFound() {
        when(postRepository.findById(eq(post.getId()))).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.deletePost(post.getId()));
        verify(postRepository, never()).save(any());
    }

    @Test
    public void testGetAllDraftPostsByUserId() {
        Long userId = 1L;
        Sort sort = Sort.by("createdAt").descending();

        when(postRepository.findAll(argThat(actualExample ->
                actualExample.getProbe().getAuthorId().equals(userId)
                        && !actualExample.getProbe().isPublished()
                        && !actualExample.getProbe().isDeleted()
        ), eq(sort))).thenReturn(List.of(post));

        List<Post> posts = postService.getAllDraftPostsByUserId(userId);

        assertNotNull(posts);
        assertTrue(posts.contains(post));
        verify(postRepository, times(1)).findAll(argThat(actualExample ->
                actualExample.getProbe().getAuthorId().equals(userId)
                        && !actualExample.getProbe().isPublished()
                        && !actualExample.getProbe().isDeleted()), eq(sort));
    }

    @Test
    public void testGetAllDraftPostsByProjectId() {
        Long projectId = 2L;
        Sort sort = Sort.by("createdAt").descending();

        when(postRepository.findAll(argThat(actualExample ->
                actualExample.getProbe().getProjectId().equals(projectId)
                        && !actualExample.getProbe().isPublished()
                        && !actualExample.getProbe().isDeleted()
        ), eq(sort))).thenReturn(List.of(post));

        List<Post> posts = postService.getAllDraftPostsByProjectId(projectId);

        assertNotNull(posts);
        assertTrue(posts.contains(post));
        verify(postRepository, times(1)).findAll(argThat(actualExample ->
                actualExample.getProbe().getProjectId().equals(projectId)
                        && !actualExample.getProbe().isPublished()
                        && !actualExample.getProbe().isDeleted()), eq(sort));
    }

    @Test
    public void testGetAllPublishedPostsByUserId() {
        Long userId = 1L;
        Sort sort = Sort.by("publishedAt").descending();

        when(postRepository.findAll(argThat(actualExample ->
                actualExample.getProbe().getAuthorId().equals(userId)
                        && actualExample.getProbe().isPublished()
                        && !actualExample.getProbe().isDeleted()
        ), eq(sort))).thenReturn(List.of(post));

        List<Post> posts = postService.getAllPublishedPostsByUserId(userId);

        assertNotNull(posts);
        assertTrue(posts.contains(post));
        verify(postRepository, times(1)).findAll(argThat(actualExample ->
                actualExample.getProbe().getAuthorId().equals(userId)
                        && actualExample.getProbe().isPublished()
                        && !actualExample.getProbe().isDeleted()), eq(sort));
    }

    @Test
    public void testGetAllPublishedPostsByProjectId() {
        Long projectId = 2L;
        Sort sort = Sort.by("publishedAt").descending();

        when(postRepository.findAll(argThat(actualExample ->
                actualExample.getProbe().getProjectId().equals(projectId)
                        && actualExample.getProbe().isPublished()
                        && !actualExample.getProbe().isDeleted()
        ), eq(sort))).thenReturn(List.of(post));

        List<Post> posts = postService.getAllPublishedPostsByProjectId(projectId);

        assertNotNull(posts);
        assertTrue(posts.contains(post));
        verify(postRepository, times(1)).findAll(argThat(actualExample ->
                actualExample.getProbe().getProjectId().equals(projectId)
                        && actualExample.getProbe().isPublished()
                        && !actualExample.getProbe().isDeleted()), eq(sort));
    }
}

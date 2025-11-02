package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private PostService postService;

    private Post post;
    private Long postId;
    private Long projectId;
    private Long userId;

    @BeforeEach
    public void setUp() {
        userId = 1L;

        projectId = 1L;

        postId = 1L;
        post = Post.builder()
                .id(postId)
                .authorId(userId)
                .build();
    }

    @Test
    public void createDraftPost_shouldCreateNullProjectId_successfully() {

        when(userContext.getUserId()).thenReturn(userId);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post resultPost = postService.createDraftPost(post);

        assertNotNull(resultPost);
        assertFalse(resultPost.isDeleted());
        assertFalse(resultPost.isPublished());
        assertEquals(userId, resultPost.getAuthorId());

        verify(projectServiceClient, times(0)).getProject(projectId);
    }

    @Test
    public void publishedPost_testPublishPost_successfully() {
        post.setPublished(false);
        post.setDeleted(false);

        when(userContext.getUserId()).thenReturn(userId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.publishedPost(postId);

        assertNotNull(result);
        assertFalse(result.isDeleted());
        assertTrue(result.isPublished());
    }

    @Test
    public void publishedPost_testPublishPostAlreadyPublished_throws() {
        post.setPublished(true);
        post.setDeleted(false);

        when(userContext.getUserId()).thenReturn(userId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(ForbiddenException.class, () -> postService.publishedPost(postId));
        verify(postRepository, times(0)).save(post);
    }

    @Test
    public void publishedPost_noUserIsPostAuthor_throws() {
        post.setPublished(false);
        post.setDeleted(false);
        post.setAuthorId(2L);
        when(userContext.getUserId()).thenReturn(userId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(ForbiddenException.class, () -> postService.publishedPost(postId));
        verify(postRepository, times(0)).save(post);
    }

    @Test
    public void publishedPost_returnPostIsNull_throws() {
        post.setPublished(false);
        post.setDeleted(false);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> postService.publishedPost(postId));
        verify(postRepository, times(0)).save(post);
    }

    @Test
    public void updatePost_successfully() {
        post.setContent("Old mes");

        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(userContext.getUserId()).thenReturn(userId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        PostUpdateDto postUpdateDto = new PostUpdateDto("update content. Super message. innovation info");

        Post result = postService.updatePost(postId, postUpdateDto);

        assertNotNull(result);
        assertEquals(result.getContent(), postUpdateDto.content());

        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void deleteById_postIsNotDeleted_successfully() {
        post.setPublished(true);
        post.setDeleted(false);

        when(userContext.getUserId()).thenReturn(userId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.save(postCaptor.capture()))
                .thenReturn(post);

        postService.deleteById(postId);

        Post result = postCaptor.getValue();

        assertTrue(result.isDeleted());

        verify(postRepository, times(1)).save(post);
    }

    @Test
    public void deleteById_postAlreadyDeleted_throws() {
        post.setPublished(true);
        post.setDeleted(true);
        when(userContext.getUserId()).thenReturn(userId);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(ForbiddenException.class, () -> postService.deleteById(postId));

        verify(postRepository, times(0)).save(post);

    }

    @Test
    public void getDraftPostByAuthorId_successfully() {
        Post post2 = Post.builder()
                .authorId(2L)
                .projectId(null)
                .id(2L)
                .deleted(false)
                .published(false)
                .createdAt(LocalDateTime.now().minusHours(3))
                .build();
        Post post3 = Post.builder()
                .authorId(2L)
                .projectId(null)
                .id(3L)
                .deleted(false)
                .published(false)
                .createdAt(LocalDateTime.now().plusDays(3))
                .build();

        List<Post> posts = List.of(post2, post3);
        Long authorId = 2L;
        when(postRepository.findPostToDraftByAuthorId(authorId)).thenReturn(posts);

        List<Post> resultPosts = postService.getDraftPostByAuthorId(authorId);

        assertEquals(2, resultPosts.size());
        assertEquals(3L, resultPosts.get(0).getId());
        assertEquals(2L, resultPosts.get(1).getId());
    }

    @Test
    public void getDraftPostByAuthorId_returnEmpty() {
        List<Post> posts = new ArrayList<>();
        Long authorId = 3L;
        when(postRepository.findPostToDraftByAuthorId(authorId)).thenReturn(posts);

        List<Post> resultPosts = postService.getDraftPostByAuthorId(authorId);

        assertEquals(0, resultPosts.size());
    }

    @Test
    public void getPublishedPostByAuthorId_successfully() {
        Post post6 = Post.builder()
                .authorId(2L)
                .projectId(null)
                .id(6L)
                .deleted(false)
                .published(true)
                .createdAt(LocalDateTime.now().plusDays(7))
                .build();
        Post post7 = Post.builder()
                .authorId(2L)
                .projectId(null)
                .id(7L)
                .deleted(false)
                .published(true)
                .createdAt(LocalDateTime.now().plusDays(11))
                .build();

        List<Post> posts = List.of(post7, post6);
        Long authorId = 2L;
        when(postRepository.findPostToPublishedByAuthorId(authorId)).thenReturn(posts);

        List<Post> resultPosts = postService.getPublishedPostByAuthorId(authorId);

        assertEquals(2, resultPosts.size());
        assertEquals(7L, resultPosts.get(0).getId());
        assertEquals(6L, resultPosts.get(1).getId());
    }

    @Test
    public void getPublishedPostByAuthorId_returnEmpty() {
        List<Post> posts = new ArrayList<>();
        Long authorId = 3L;
        when(postRepository.findPostToPublishedByAuthorId(authorId)).thenReturn(posts);

        List<Post> resultPosts = postService.getPublishedPostByAuthorId(authorId);

        assertEquals(0, resultPosts.size());
    }

}
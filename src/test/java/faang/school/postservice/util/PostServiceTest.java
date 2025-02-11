package faang.school.postservice.util;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.InternalServices;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private InternalServices internalServices;

    @InjectMocks
    private PostService postService;

    private static Post post;
    private static Post projectPost;
    private static Post originalPost;
    private static Post post1;
    private static Post post2;

    @BeforeAll
    public static void SetUp() {
        post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);

        projectPost = new Post();
        projectPost.setId(1L);
        projectPost.setProjectId(1L);

        originalPost = new Post();
        originalPost.setId(1L);
        originalPost.setAuthorId(1L);

        post1 = new Post();
        post1.setId(1L);
        post1.setAuthorId(1L);
        post1.setProjectId(1L);
        post1.setDeleted(false);
        post1.setPublished(false);
        post1.setCreatedAt(LocalDateTime.now().minusDays(1));

        post2 = new Post();
        post2.setId(2L);
        post2.setAuthorId(1L);
        post2.setProjectId(1L);
        post2.setDeleted(false);
        post2.setPublished(false);
        post2.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @Order(1)
    public void createDraft_ValidAuthor() {
        when(internalServices.userExists(1L)).thenReturn(true);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.createDraft(post);

        assertNotNull(result);
    }

    @Test
    @Order(2)
    public void createDraft_InvalidAuthor() {
        when(internalServices.userExists(1L)).thenReturn(false);

        assertThrows(InvalidParameterException.class, () -> postService.createDraft(post));
    }

    @Test
    @Order(3)
    public void createDraft_InvalidProject() {
        when(internalServices.projectExists(1L)).thenReturn(false);

        assertThrows(InvalidParameterException.class, () -> postService.createDraft(projectPost));
    }

    @Test
    @Order(4)
    public void publish_Valid() {
        post.setPublished(false);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.publish(1L);

        assertTrue(result.isPublished());
        assertNotNull(result.getPublishedAt());
    }

    @Test
    @Order(5)
    public void publish_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> postService.publish(1L));
    }

    @Test
    @Order(6)
    public void publish_AlreadyPublished() {
        post.setPublished(true);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> postService.publish(1L));
    }

    @Test
    @Order(7)
    public void update_Valid() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(originalPost));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.update(post);

        assertNotNull(result);
    }

    @Test
    @Order(8)
    public void update_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> postService.update(post));
    }

    @Test
    @Order(9)
    public void update_AuthorChanged() {
        post.setAuthorId(2L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(originalPost));

        assertThrows(DataValidationException.class, () -> postService.update(post));
    }

    @Test
    @Order(10)
    public void delete_Valid() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertDoesNotThrow(() -> postService.delete(1L));
    }

    @Test
    @Order(11)
    public void delete_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> postService.delete(1L));
    }

    @Test
    @Order(12)
    public void get_ValidPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post result = postService.get(1L);

        assertNotNull(result);
    }

    @Test
    @Order(13)
    public void get_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> postService.get(1L));
    }

    @Test
    @Order(14)
    public void getDraftsByAuthorId_Valid() {
        when(postRepository.findByAuthorId(anyLong())).thenReturn(List.of(post1, post2));

        List<Post> result = postService.getDraftsByAuthorId(1L);

        assertEquals(2, result.size());
        assertEquals(post2, result.get(0));
        assertEquals(post1, result.get(1));
    }

    @Test
    @Order(15)
    public void getDraftsByProjectId_Valid() {
        when(postRepository.findByProjectId(anyLong())).thenReturn(List.of(post1, post2));

        List<Post> result = postService.getDraftsByProjectId(1L);

        assertEquals(2, result.size());
        assertEquals(post2, result.get(0));
        assertEquals(post1, result.get(1));
    }

    @Test
    @Order(16)
    public void getPostsByAuthorId_Valid() {
        post1.setPublished(true);
        post1.setPublishedAt(LocalDateTime.now().minusDays(1));
        post2.setPublished(true);
        post2.setPublishedAt(LocalDateTime.now());

        when(postRepository.findByAuthorId(anyLong())).thenReturn(List.of(post1, post2));

        List<Post> result = postService.getPostsByAuthorId(1L);

        assertEquals(2, result.size());
        assertEquals(post2, result.get(0)); // post2 is more recent
        assertEquals(post1, result.get(1));
    }

    @Test
    @Order(17)
    public void getPostsByProjectId_Valid() {
        post1.setPublished(true);
        post1.setPublishedAt(LocalDateTime.now().minusDays(1));
        post2.setPublished(true);
        post2.setPublishedAt(LocalDateTime.now());

        when(postRepository.findByProjectId(anyLong())).thenReturn(List.of(post1, post2));

        List<Post> result = postService.getPostsByProjectId(1L);

        assertEquals(2, result.size());
        assertEquals(post2, result.get(0)); // post2 is more recent
        assertEquals(post1, result.get(1));
    }
}

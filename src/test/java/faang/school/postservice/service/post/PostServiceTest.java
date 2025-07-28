package faang.school.postservice.service.post;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private static final Long POST_ID = 1L;

    @Test
    @DisplayName("Should return post when post with given ID exists")
    void getPostByIdReturnsPostWhenExists() {
        Post post = new Post();
        post.setId(POST_ID);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        Post result = postService.getPostById(POST_ID);

        assertNotNull(result);
        assertEquals(POST_ID, result.getId());
        verify(postRepository).findById(POST_ID);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when post not found")
    void getPostByIdThrowsWhenNotFound() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postService.getPostById(POST_ID)
        );

        assertEquals("Post not found with id " + POST_ID, exception.getMessage());
        verify(postRepository).findById(POST_ID);
    }

    @Test
    @DisplayName("Should return true when post exists by ID")
    void existsByIdReturnsTrue() {
        when(postRepository.existsById(POST_ID)).thenReturn(true);

        boolean exists = postService.existsById(POST_ID);

        assertTrue(exists);
        verify(postRepository).existsById(POST_ID);
    }

    @Test
    @DisplayName("Should return false when post does not exist by ID")
    void existsByIdReturnsFalse() {
        when(postRepository.existsById(POST_ID)).thenReturn(false);

        boolean exists = postService.existsById(POST_ID);

        assertFalse(exists);
        verify(postRepository).existsById(POST_ID);
    }
}

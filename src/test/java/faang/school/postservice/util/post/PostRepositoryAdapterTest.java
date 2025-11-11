package faang.school.postservice.util.post;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostRepositoryAdapterTest {

    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private PostRepositoryAdapter postRepositoryAdapter;

    @Test
    public void testPostNotFound() {
        final long postId = 5L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postRepositoryAdapter.getPostById(postId));

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    public void testPostFound() {
        final long postId = 5L;
        final Post post = new Post();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post foundedPost = postRepositoryAdapter.getPostById(postId);

        assertNotNull(foundedPost);
        assertEquals(post, foundedPost);

        verify(postRepository, times(1)).findById(postId);
    }
}

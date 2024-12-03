package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.RedisMessagePublisher;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @Test
    public void testGetPostNotFound() {
        long id = 1L;

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> postService.getPost(id));
    }

    @Test
    public void testGetPostSuccessful() {
        long id = 1L;

        when(postRepository.findById(id)).thenReturn(Optional.of(new Post()));
        postService.getPost(id);
    }

    @Test
    public void testExistsPostByIdWhenIdIsNull() {
        assertFalse(postService.existsPostById(null));
    }

    @Test
    public void testExistsPostById() {
        long id = 1L;

        when(postRepository.existsById(id)).thenReturn(true);
        assertTrue(postService.existsPostById(id));
    }

    @Test
    void testCheckAndBanAuthors_WithMoreThanFiveUnverifiedPost() {
        long authorId = 1;
        List<Post> unverifiedPosts = List.of(
                createPost(authorId, false),
                createPost(authorId, false),
                createPost(authorId, false),
                createPost(authorId, false),
                createPost(authorId, false),
                createPost(authorId, false)
        );

        when(postRepository.findByVerifiedFalse()).thenReturn(unverifiedPosts);

        postService.checkAndBanAuthors();

        verify(redisMessagePublisher, times(1)).publish(authorId);
    }

    @Test
    void testCheckAndBanAuthors_WithLessThanFiveUnverifiedPost() {
        long authorId = 1;
        List<Post> unverifiedPosts = List.of(
                createPost(authorId, false),
                createPost(authorId, false),
                createPost(authorId, false)
        );

        when(postRepository.findByVerifiedFalse()).thenReturn(unverifiedPosts);

        postService.checkAndBanAuthors();

        verify(redisMessagePublisher, times(0)).publish(authorId);
    }

    @Test
    void testCheckAndBanAuthors_WithoutUnverifiedPosts() {
        when(postRepository.findByVerifiedFalse()).thenReturn(Collections.emptyList());

        postService.checkAndBanAuthors();

        verify(redisMessagePublisher, times(0)).publish(anyLong());
    }

    private Post createPost(Long authorId, boolean verified) {
        Post post = new Post();
        post.setAuthorId(authorId);
        post.setVerified(verified);
        return post;
    }
}

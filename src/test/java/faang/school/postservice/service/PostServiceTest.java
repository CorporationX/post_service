package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.post.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;


    @Test
    @DisplayName("getPost(). Positive")
    void getPostPositive() {
        Long postId = 1234567L;
        Post post = new Post();
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post postReturned = postService.getPost(postId);

        assertEquals(post, postReturned);
        Mockito.verify(postRepository, Mockito.times(1)).findById(any(Long.class));
    }

    @Test
    @DisplayName("getPost(). Negative")
    void getPostNegative() {
        Long postId = 1234567L;
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> postService.getPost(postId));
        Mockito.verify(postRepository, Mockito.times(1)).findById(any(Long.class));
    }
}
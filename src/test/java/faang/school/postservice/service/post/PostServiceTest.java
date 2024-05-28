package faang.school.postservice.service.post;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    private Post post;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .content("Test post")
                .build();
    }

    @Test
    void getPostByIdValidIdShouldReturnPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        Post result = postService.getPostById(1L);
        assertEquals(post, result);
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void getPostByIdInvalidIdShouldThrowEntityNotFoundException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(1L));
        verify(postRepository, times(1)).findById(1L);
    }
}
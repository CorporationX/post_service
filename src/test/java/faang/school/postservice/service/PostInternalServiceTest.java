package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostInternalServiceTest {

    private PostRepository postRepository;
    private PostInternalService postInternalService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        postInternalService = new PostInternalService(postRepository);
    }

    @Test
    void findPostById_shouldReturnPost_whenExists() {
        Post post = new Post();
        post.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post result = postInternalService.findPostById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(postRepository).findById(1L);
    }

    @Test
    void findPostById_shouldThrowException_whenNotFound() {
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> postInternalService.findPostById(2L)
        );

        assertEquals("There is no such id = 2", exception.getMessage());
        verify(postRepository).findById(2L);
    }
}
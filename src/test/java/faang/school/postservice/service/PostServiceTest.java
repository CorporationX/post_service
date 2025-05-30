package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private PostService postService;

    @Test
    void testGetPost() {
        Post post = new Post();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        assertEquals(post, postService.getPost(1L));
    }

    @Test
    void testPostIsNotFound() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> postService.getPost(1L));
        assertEquals("Post not found!", dataValidationException.getMessage());
    }
}
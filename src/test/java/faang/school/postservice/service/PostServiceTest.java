package faang.school.postservice.service;

import faang.school.postservice.exception.NotFoundException;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Test
    void testGetById() {
        long id = 1L;
        Post post = Post.builder()
                .id(id)
                .build();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        Post result = postService.getById(id);

        assertEquals(post, result);
        verify(postRepository, times(1)).findById(id);
    }

    @Test
    void testGetById_notExists_throws() {
        long id = 1L;

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.getById(id));
        verify(postRepository, times(1)).findById(id);
    }
}
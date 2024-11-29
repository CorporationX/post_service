package faang.school.postservice.service.post;

import faang.school.postservice.exception.post.PostNotFoundException;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostQueryServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostViewCounterService postViewCounterService;

    @InjectMocks
    private PostQueryService postQueryService;

    @Test
    void testFindPostById_Success() {
        Post post = Post.builder().id(1L).content("Test content").build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post result = postQueryService.findPostById(1L);

        assertEquals(post, result);
        verify(postRepository).findById(1L);
        verify(postViewCounterService).incrementViewCount(1L);
    }

    @Test
    void testFindPostById_NotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postQueryService.findPostById(1L));
        verify(postRepository).findById(1L);
        verify(postViewCounterService, never()).incrementViewCount(anyLong());
    }
}
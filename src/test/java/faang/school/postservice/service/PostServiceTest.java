package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private PostService postService;

    @Test
    void getPostById(){
        long id = 1L;
        Post post = Post.builder().id(id).build();

        Mockito.when(postRepository.findById(id)).thenReturn(Optional.of(post));

        Post result  = postService.getPostById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getUserByIdTestException(){
        long id = -1L;
        Mockito.when(postRepository.findById(id))
                .thenThrow(new IllegalArgumentException("There is no such id = " + id));

        assertThrows(IllegalArgumentException.class, ()->postRepository.findById(id));
    }
}
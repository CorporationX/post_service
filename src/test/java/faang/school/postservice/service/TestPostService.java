package faang.school.postservice.service;

import faang.school.postservice.mapper.PostContextMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TestPostService {

    @InjectMocks
    private PostService service;

    @Mock
    private PostRepository repository;
    @Mock
    private PostContextMapper context;

    @Test
    public void testGetPostWhenNotDataBase() {
        long postId = 1;
        when(repository.findById(postId)).thenReturn(Optional.empty());
        long countLike = 1;

        service.getPost(postId);

        verify(context, times(0)).getCountLikeEveryonePost().put(postId, countLike);
        assertThrows(IllegalArgumentException.class, () -> service.getPost(postId));
    }

    @Test
    public void testGetPostWhenValid() {
        long postId = 1;
        Post post = new Post();
        post.setId(postId);
        post.setLikes(Arrays.asList(new Like(), new Like()));
        when(repository.findById(postId)).thenReturn(Optional.of(post));
        long countLike = post.getLikes().size();

        service.getPost(postId);

        verify(context, times(1)).getCountLikeEveryonePost().put(postId, countLike);
        assertDoesNotThrow(() -> service.getPost(postId));
    }
}

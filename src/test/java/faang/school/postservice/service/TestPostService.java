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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class TestPostService {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostContextMapper context;

    @Test
    public void testGetPostWhenNotDataBase() {
        long postId = 1;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> postService.getPost(postId));
        verify(context, never()).getCountLikeEveryonePost();
    }

    @Test
    public void testGetPostWhenValid() {
        long postId = 1;
        Post post = new Post();
        post.setId(postId);
        post.setLikes(Arrays.asList(new Like(), new Like()));
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getPost(postId);

        assertDoesNotThrow(() -> postService.getPost(postId));
        assertEquals(post, result);
    }
}

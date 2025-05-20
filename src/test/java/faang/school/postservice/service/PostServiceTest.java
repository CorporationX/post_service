package faang.school.postservice.service;

import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Spy
    private Utils utils;

    @InjectMocks
    private PostService postService;

    @Test
    public void findPostByIdSuccess() {
        Long postId = 10L;
        Post mockPost = Post.builder()
                .id(postId)
                .content("mock post")
                .build();
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(mockPost));

        Post actualPost = postService.findPostById(postId);
        assertNotNull(actualPost);
        assertNotNull(mockPost);
        assertEquals(mockPost.getId(), actualPost.getId());
    }

    @Test
    public void findPostByIdFail() {
        Long postId = 10L;
        String expected = utils.format(PostService.POST_BY_ID_NOT_FOUND, postId);
        Mockito.when(postRepository.findById(postId)).thenReturn(Optional.empty());

        PostNotFoundException result = assertThrows(
                PostNotFoundException.class, () -> postService.findPostById(postId));
        assertEquals(expected, result.getMessage());
    }
}
package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private Long postId;

    @BeforeEach
    void setUp() {
        postId = 1L;
        testPost = Post.builder()
                .id(postId)
                .content("Test content")
                .authorId(123L)
                .build();
    }

    @Test
    void testFindById_PostExists() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(testPost));

        Optional<Post> result = postService.findById(postId);

        assertTrue(result.isPresent());
        assertEquals(testPost, result.get());
    }

    @Test
    void testFindById_PostDoesNotExist() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        Optional<Post> result = postService.findById(postId);

        assertTrue(result.isEmpty());
    }
}
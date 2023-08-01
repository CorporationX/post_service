package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;

    @Test
    void testGetPostById_ExistingPostId_ReturnsPost() {
        Long postId = 1L;
        Post existingPost = Post.builder()
                .id(postId)
                .content("Test post content")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        Post retrievedPost = postService.getPostById(postId);

        assertNotNull(retrievedPost);
        assertEquals(existingPost.getId(), retrievedPost.getId());
        assertEquals(existingPost.getContent(), retrievedPost.getContent());

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testGetPostById_NonExistingPostId_ThrowsEntityNotFoundException() {
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(postId));

        verify(postRepository, times(1)).findById(postId);
    }
}
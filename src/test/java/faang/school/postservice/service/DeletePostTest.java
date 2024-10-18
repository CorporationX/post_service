package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.publisher.PostViewPublisher;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeletePostTest {
    @Mock
    UserContext userContext;

    @Mock
    PostViewPublisher postViewPublisher;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);
        post.setDeleted(false);
    }

    @Test
    void shouldDeletePost() {
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.of(post));

        postService.deletePost(1L);

        verify(postRepository).findById(1L);
        verify(postRepository).save(post);
        assertTrue(post.isDeleted(), "Post should be marked as deleted");
    }

    @Test
    void shouldThrowExceptionWhenPostNotFound() {
        when(postRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ResponseStatusException.class, () -> postService.deletePost(1L));
    }
}
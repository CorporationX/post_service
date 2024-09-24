package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostPublisherTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostPublisher postPublisher;

    @Test
    @DisplayName("Publish scheduled posts")
    public void testPublishScheduledPosts() {
        Post post = Post.builder()
                .id(0L)
                .published(false)
                .scheduledAt(LocalDateTime.of(2024, 9, 21, 17, 30))
                .build();
        Post post2 = Post.builder()
                .id(1L)
                .published(false)
                .scheduledAt(LocalDateTime.of(2024, 9, 21, 17, 32))
                .build();
        List<Post> posts = List.of(post, post2);
        when(postRepository.findReadyToPublish()).thenReturn(posts);
        when(postRepository.saveAll(posts)).thenReturn(posts);

        postPublisher.publishScheduledPosts();

        verify(postRepository).findReadyToPublish();
        verify(postRepository).saveAll(posts);
        assertTrue(post.isPublished());
        assertTrue(post2.isPublished());
        assertNotNull(post.getPublishedAt());
        assertNotNull(post2.getPublishedAt());
    }
}

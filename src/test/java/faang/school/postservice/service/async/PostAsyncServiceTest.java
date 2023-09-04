package faang.school.postservice.service.async;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostAsyncServiceTest {
    @InjectMocks
    private PostAsyncService postAsyncService;
    @Mock
    private PostRepository postRepository;
    private List<Post> posts;
    private Post post;
    private LocalDateTime time;

    @BeforeEach
    void setUp() {
        time = LocalDateTime.now().minusMinutes(1);
        post = Post.builder()
                .id(1L)
                .content("content")
                .publishedAt(time)
                .published(false)
                .build();
        posts = List.of(post);
    }

    @Test
    void testPublishPosts() {
        when(postRepository.save(post)).thenReturn(post);

        postAsyncService.publishPosts(posts);

        verify(postRepository, times(1)).save(post);
        assertTrue(post.isPublished());
        assertTrue(post.getPublishedAt().isAfter(time));
    }
}
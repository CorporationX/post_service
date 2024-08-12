package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostPublishService;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostPublishService postPublishService;

    @InjectMocks
    private PostService postService;

    @Test
    void publishScheduledPosts_success() throws Exception {
        Post post1 = new Post();
        post1.setPublished(false);
        post1.setPublishedAt(LocalDateTime.now().minusMinutes(1));

        Post post2 = new Post();
        post2.setPublished(false);
        post2.setPublishedAt(LocalDateTime.now().minusMinutes(2));

        Post post3 = new Post();
        post3.setPublished(false);
        post3.setPublishedAt(LocalDateTime.now().minusMinutes(3));

        List<Post> scheduledPosts = Arrays.asList(post1, post2, post3);

        ReflectionTestUtils.setField(postService, "postsBatchSize", 1);

        when(postRepository.findReadyToPublish()).thenReturn(scheduledPosts);

        when(postPublishService.publishBatch(anyList()))
                .thenAnswer(invocation -> CompletableFuture.runAsync(() -> {
                    List<Post> posts = invocation.getArgument(0);
                    posts.forEach(post -> post.setPublished(true));
                }));

        postService.publishScheduledPosts();

        for (Post post : scheduledPosts) {
            assertTrue(post.isPublished());
        }

        verify(postPublishService, times(3)).publishBatch(any());
        verify(postRepository, times(3)).saveAll(any());
    }
}
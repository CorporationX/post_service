package faang.school.postservice.publisher;

import faang.school.postservice.config.ThreadPoolConfig;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private ThreadPoolConfig threadPoolConfig;
    @Mock
    private ExecutorService poolForScheduled;
    @Captor
    private ArgumentCaptor<List<Post>> captor;
    @InjectMocks
    private PostService postService;

    private ExecutorService poolForTest = Executors.newFixedThreadPool(10);

    @Test
    @DisplayName("положительный кейс")
    void testPublishScheduledPosts() {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 3000; i++) {
            Post post = new Post();
            post.setPublished(false);
            posts.add(post);
        }

        when(postRepository.findReadyToPublish()).thenReturn(posts);
        when(threadPoolConfig.poolForScheduled()).thenReturn(poolForTest);

        postService.publishScheduledPosts();
        verify(postRepository, times(4)).saveAll(captor.capture());
    }

}

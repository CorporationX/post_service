package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.async.PostServiceAsyncImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

@ExtendWith(MockitoExtension.class)
class PostServiceAsyncImplTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceAsyncImpl postServiceAsyncImpl;

    @Test
    @DisplayName("Publish Scheduled Posts Async")
    void testPublishScheduledPostsAsyncInBatch() {
        postServiceAsyncImpl.publishScheduledPostsAsyncInBatch(List.of(new Post()));
        Mockito.verify(postRepository).updatePostsAsPublished(anyList(), any(LocalDateTime.class));
    }
}
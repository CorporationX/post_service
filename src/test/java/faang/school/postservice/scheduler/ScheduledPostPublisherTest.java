package faang.school.postservice.scheduler;

import faang.school.postservice.service.impl.post.PostServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScheduledPostPublisherTest {

    @Mock
    private PostServiceImpl postService;

    @InjectMocks
    private ScheduledPostPublisher scheduledPostPublisher;

    @Test
    @DisplayName("Publish Scheduled Posts")
    void testPublishScheduledPosts() {
        scheduledPostPublisher.publishScheduledPosts();
        verify(postService).publishScheduledPosts(anyInt());
    }
}
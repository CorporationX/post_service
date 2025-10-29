package faang.school.postservice.scheduler;

import faang.school.postservice.scheduler.post.ScheduledPostPublisherImpl;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class ScheduledPostPublisherImplTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private ScheduledPostPublisherImpl scheduledPostPublisher;

    @Test
    @DisplayName("publishScheduledPosts invokes PostService once")
    void publishScheduledPosts_shouldInvokePostServiceOnce() {
        scheduledPostPublisher.publishScheduledPosts();
        verify(postService, times(1)).publishScheduledPosts();
        verifyNoMoreInteractions(postService);
    }
}
package faang.school.postservice.schedule;

import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScheduledPostPublisherTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private ScheduledPostPublisher scheduledPostPublisher;

    @Test
    void publishScheduledPosts_InvokesPostServiceWhenCalled() {
        scheduledPostPublisher.publishScheduledPosts();

        verify(postService, times(1)).publishScheduledPosts();
    }

    @Test
    void publishScheduledPosts_LogsErrorWhenServiceThrowsException() {
        doThrow(new RuntimeException("Service error")).when(postService).publishScheduledPosts();

        scheduledPostPublisher.publishScheduledPosts();

        verify(postService, times(1)).publishScheduledPosts();
    }
}

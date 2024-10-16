package faang.school.postservice.service.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledPostPublisherTest {
    private static final Integer POST_PUBLISH_BATCH_SIZE = 100;

    @Mock
    private PostService postService;
    @InjectMocks
    private ScheduledPostPublisher scheduledPostPublisher;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(scheduledPostPublisher, "postPublishBatchSize", POST_PUBLISH_BATCH_SIZE);
    }

    @Test
    void testScheduledPostPublish() {
        int readyToPublishPosts = 1000;
        int times = readyToPublishPosts / POST_PUBLISH_BATCH_SIZE;
        when(postService.getReadyToPublish()).thenReturn(readyToPublishPosts);

        scheduledPostPublisher.scheduledPostPublish();
        verify(postService).getReadyToPublish();
        verify(postService, times(times)).processReadyToPublishPosts(POST_PUBLISH_BATCH_SIZE);
    }
}
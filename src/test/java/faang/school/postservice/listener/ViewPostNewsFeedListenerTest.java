package faang.school.postservice.listener;

import faang.school.postservice.protobuf.generate.FeedEventProto;
import faang.school.postservice.service.cache.SingleCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ViewPostNewsFeedListenerTest {

    @Mock
    private SingleCacheService<Long, Long> viewsCacheService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private ViewPostNewsFeedListener viewPostNewsFeedListener;

    @Test
    void onMessage_shouldSaveViewSuccessfully() {
        FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.newBuilder()
                .setPostId(123L)
                .setAuthorId(456L)
                .build();
        byte[] byteEvent = feedEvent.toByteArray();

        viewPostNewsFeedListener.onMessage(byteEvent, acknowledgment);

        verify(viewsCacheService).save(123L, 456L);
        verify(acknowledgment).acknowledge();
    }
}
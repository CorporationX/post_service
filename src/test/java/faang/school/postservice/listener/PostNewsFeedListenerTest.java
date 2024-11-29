package faang.school.postservice.listener;

import faang.school.postservice.protobuf.generate.FeedEventProto;
import faang.school.postservice.service.cache.AsyncCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostNewsFeedListenerTest {

    @Mock
    private AsyncCacheService<Long, Long> asyncCacheFeedRepository;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private PostNewsFeedListener postNewsFeedListener;

    @Test
    void onMessage_shouldProcessAndSaveFollowersSuccessfully() {
        List<Long> followerIds = List.of(1L, 2L, 3L);
        Long postId = 123L;
        FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.newBuilder()
                .addAllFollowerIds(followerIds)
                .setPostId(postId)
                .build();
        byte[] byteFeedEvent = feedEvent.toByteArray();

        when(asyncCacheFeedRepository.save(anyLong(), anyLong()))
                .thenReturn(CompletableFuture.completedFuture(null));

        postNewsFeedListener.onMessage(byteFeedEvent, acknowledgment);

        verify(asyncCacheFeedRepository, times(followerIds.size())).save(anyLong(), eq(postId));
        verify(acknowledgment).acknowledge();
    }
}
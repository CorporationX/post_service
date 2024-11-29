package faang.school.postservice.listener;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import faang.school.postservice.service.cache.SingleCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeNewsFeedListenerTest {

    @Mock
    private SingleCacheService<Long, LikeDto> likeCache;

    @Spy
    private LikeMapperImpl likeMapper;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private LikeNewsFeedListener likeNewsFeedListener;

    @Test
    void onMessage_shouldProcessAndSaveLikeSuccessfully() {
        long postId = 123L;
        FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.newBuilder()
                .setPostId(postId)
                .build();
        byte[] byteEvent = feedEvent.toByteArray();

        likeNewsFeedListener.onMessage(byteEvent, acknowledgment);

        verify(likeMapper).toLikeDto(feedEvent);
        verify(likeCache).save(eq(postId), any(LikeDto.class));
        verify(acknowledgment).acknowledge();
    }
}

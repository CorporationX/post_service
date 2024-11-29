package faang.school.postservice.listener;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
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
class CommentNewsFeedListenerTest {

    @Mock
    private SingleCacheService<Long, CommentDto> cacheService;

    @Spy
    private CommentMapperImpl commentMapper;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private CommentNewsFeedListener commentNewsFeedListener;

    @Test
    void onMessage_shouldProcessAndSaveCommentSuccessfully() {
        long postId = 1L;
        FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.newBuilder()
                .setPostId(postId)
                .build();
        byte[] byteFeedEvent = feedEvent.toByteArray();

        commentNewsFeedListener.onMessage(byteFeedEvent, acknowledgment);

        verify(commentMapper).toDto(feedEvent);
        verify(cacheService).save(eq(postId), any(CommentDto.class));
        verify(acknowledgment).acknowledge();
    }

}

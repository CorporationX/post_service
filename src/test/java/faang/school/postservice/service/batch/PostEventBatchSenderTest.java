package faang.school.postservice.service.batch;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.batch.BatchProperties;
import faang.school.postservice.dto.event.PostFeedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostEventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostEventBatchSenderTest {

    private Post post;
    private List<Long> subscribers;
    private Long authorId;

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostEventProducer postEventProducer;
    @Mock
    private BatchProperties batchProperties;

    @InjectMocks
    private PostEventBatchSender postEventBatchSender;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .content("content")
                .authorId(2L)
                .projectId(3L)
                .scheduledAt(LocalDateTime.of(2021, 7, 15, 10, 30))
                .build();

        subscribers = LongStream.rangeClosed(1, 12).boxed().toList();

        postEventBatchSender = new PostEventBatchSender(userServiceClient, postEventProducer, batchProperties);

        authorId = post.getAuthorId();
    }

    @Test
    void testBatchSending_WhenArgsValid_SuccessfulCompletion() {
        when(userServiceClient.getFollowerIds(authorId)).thenReturn(subscribers);
        when(batchProperties.getBatchSizeSubscribers()).thenReturn(10);

        postEventBatchSender.sendBatch(post);

        verify(userServiceClient).getFollowerIds(authorId);

        ArgumentCaptor<PostFeedEvent> captor = ArgumentCaptor.forClass(PostFeedEvent.class);
        verify(postEventProducer, times(2)).sendEvent(captor.capture());

        PostFeedEvent postEventCaptor = captor.getValue();

        assertEquals(postEventCaptor.getPostId(), post.getId());
        assertEquals(postEventCaptor.getAuthorId(), authorId);
    }

    @Test
    void testCreatePost_WhenUserSubscribersIsEmpty_ReturnIllegalArgumentException() {
        when(userServiceClient.getFollowerIds(authorId)).thenReturn(Collections.emptyList());

        postEventBatchSender.sendBatch(post);

        verify(postEventProducer, never()).sendEvent(any());
    }
}
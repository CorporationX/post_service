package faang.school.postservice.service.batches;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.properties.BatchProperties;
import faang.school.postservice.dto.event.PostFeedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.post.PostEventProducer;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostEventBatchSenderTest {

    private Post post;
    private List<Long> subscribers;

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

        subscribers = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L);

        postEventBatchSender = new PostEventBatchSender(userServiceClient, postEventProducer, batchProperties);
    }

    @Test
    void testBatchSending_WhenArgsValid_SuccessfulCompletion() {
        when(userServiceClient.getFollowerIds(post.getAuthorId())).thenReturn(subscribers);
        when(batchProperties.getBatchSizeSubscribers()).thenReturn(10);

        postEventBatchSender.sendBatch(post);

        verify(userServiceClient).getFollowerIds(post.getAuthorId());

        ArgumentCaptor<PostFeedEvent> captor = ArgumentCaptor.forClass(PostFeedEvent.class);
        verify(postEventProducer, times(2)).sendEvent(captor.capture());

        PostFeedEvent postEventCaptor = captor.getValue();

        assertEquals(postEventCaptor.getPostId(), post.getId());
        assertEquals(postEventCaptor.getAuthorId(), post.getAuthorId());
    }

    @Test
    void testCreatePost_WhenUserSubscribersIsEmpty_ReturnIllegalArgumentException() {
        when(userServiceClient.getFollowerIds(post.getAuthorId())).thenReturn(Collections.emptyList());

        postEventBatchSender.sendBatch(post);

        verify(postEventProducer, never()).sendEvent(any());
    }
}
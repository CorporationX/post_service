package faang.school.postservice.kafka.producer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.kafka.event.FeedUpdateEvent;
import faang.school.postservice.kafka.event.NewPostEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaPostProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private KafkaPostProducer kafkaPostProducer;

    @Captor
    private ArgumentCaptor<FeedUpdateEvent> feedEventCaptor;

    private NewPostEvent testPostEvent;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaPostProducer, "batchSize", 2);

        testPostEvent = NewPostEvent.builder()
                .postId(1L)
                .authorId(1L)
                .content("Test post")
                .build();

        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        doReturn(future).when(kafkaTemplate).send(any(), any());
    }

    @Test
    void sendPostCreatedEvent_WithAuthorAndSubscribers_ShouldSendAllEvents() {
        List<Long> subscribers = Arrays.asList(1L, 2L, 3L);
        when(userServiceClient.getUserSubscribersIds(testPostEvent.getAuthorId()))
                .thenReturn(subscribers);

        kafkaPostProducer.sendPostCreatedEvent(testPostEvent);

        verify(kafkaTemplate).send("new-posts", testPostEvent);
        verify(kafkaTemplate, times(2)).send(eq("feed-updates"), any(FeedUpdateEvent.class));

        verify(kafkaTemplate, times(3)).send(anyString(), any()); // 1 post event + 2 feed batches
    }

    @Test
    void sendPostCreatedEvent_WithProjectPost_ShouldGetProjectSubscribers() {
        NewPostEvent projectPost = NewPostEvent.builder()
                .postId(1L)
                .projectId(1L)
                .build();

        List<Long> subscribers = Arrays.asList(1L, 2L);
        when(userServiceClient.getProjectSubscriptions(projectPost.getProjectId()))
                .thenReturn(subscribers);

        kafkaPostProducer.sendPostCreatedEvent(projectPost);

        verify(userServiceClient).getProjectSubscriptions(projectPost.getProjectId());
        verify(kafkaTemplate).send("new-posts", projectPost);
        verify(kafkaTemplate).send(eq("feed-updates"), feedEventCaptor.capture());

        FeedUpdateEvent capturedEvent = feedEventCaptor.getValue();
        assertEquals(projectPost.getPostId(), capturedEvent.getPostId());
        assertEquals(subscribers, capturedEvent.getSubscriberIds());
    }

    @Test
    void sendPostCreatedEvent_WithNoSubscribers_ShouldOnlySendPostEvent() {
        when(userServiceClient.getUserSubscribersIds(testPostEvent.getAuthorId()))
                .thenReturn(Collections.emptyList());

        kafkaPostProducer.sendPostCreatedEvent(testPostEvent);

        verify(kafkaTemplate).send("new-posts", testPostEvent);
        verify(kafkaTemplate, times(1)).send(anyString(), any());
    }

    @Test
    void sendPostCreatedEvent_WithNoAuthorAndProject_ShouldHandleGracefully() {
        NewPostEvent invalidPost = NewPostEvent.builder()
                .postId(1L)
                .build();

        kafkaPostProducer.sendPostCreatedEvent(invalidPost);

        verify(kafkaTemplate).send("new-posts", invalidPost);
        verify(kafkaTemplate, times(1)).send(anyString(), any());
    }

    @Test
    void sendPostCreatedEvent_WhenKafkaFails_ShouldThrowException() {
        RuntimeException kafkaError = new RuntimeException("Kafka error");
        when(kafkaTemplate.send("new-posts", testPostEvent))
                .thenThrow(kafkaError);

        assertThrows(RuntimeException.class, () ->
                kafkaPostProducer.sendPostCreatedEvent(testPostEvent)
        );
    }

    @Test
    void sendPostCreatedEvent_WhenUserServiceFails_ShouldThrowException() {
        RuntimeException userServiceError = new RuntimeException("User service error");
        when(userServiceClient.getUserSubscribersIds(anyLong()))
                .thenThrow(userServiceError);

        assertThrows(RuntimeException.class, () ->
                kafkaPostProducer.sendPostCreatedEvent(testPostEvent)
        );
    }

    @Test
    void sendPostCreatedEvent_WithLargeSubscriberList_ShouldProcessInBatches() {
        List<Long> subscribers = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        when(userServiceClient.getUserSubscribersIds(testPostEvent.getAuthorId()))
                .thenReturn(subscribers);

         kafkaPostProducer.sendPostCreatedEvent(testPostEvent);

        verify(kafkaTemplate, times(3)).send(eq("feed-updates"), feedEventCaptor.capture());

        List<FeedUpdateEvent> capturedEvents = feedEventCaptor.getAllValues();
        assertEquals(3, capturedEvents.size());

        assertEquals(2, capturedEvents.get(0).getSubscriberIds().size());
        assertEquals(2, capturedEvents.get(1).getSubscriberIds().size());
        assertEquals(1, capturedEvents.get(2).getSubscriberIds().size());
    }

    @Test
    void sendPostCreatedEvent_WhenEventIsNull_ShouldThrowNullPointerException() {
        NewPostEvent nullEvent = null;
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                kafkaPostProducer.sendPostCreatedEvent(nullEvent)
        );

        assertEquals("Cannot invoke \"faang.school.postservice.kafka.event.NewPostEvent.getPostId()\" because \"event\" is null", exception.getMessage());
    }
}
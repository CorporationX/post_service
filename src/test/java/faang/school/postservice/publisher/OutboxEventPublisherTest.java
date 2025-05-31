package faang.school.postservice.publisher;

import faang.school.postservice.config.properties.OutboxEventPublisherProperties;
import faang.school.postservice.model.outbox.AggregateType;
import faang.school.postservice.model.outbox.EventType;
import faang.school.postservice.model.outbox.OutboxFeedEvent;
import faang.school.postservice.repository.outbox.OutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OutboxEventPublisherProperties properties;

    @Spy
    private ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

    @InjectMocks
    private OutboxEventPublisher outboxEventPublisher;

    @Captor
    private ArgumentCaptor<OutboxFeedEvent> eventCaptor;

    @Captor
    private ArgumentCaptor<LocalDateTime> dateTimeCaptor;

    private OutboxFeedEvent testEvent1;
    private OutboxFeedEvent testEvent2;

    @BeforeEach
    void setUp() {
        executor.initialize();
        lenient().doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(executor).execute(any(Runnable.class));

        UUID eventId1 = UUID.randomUUID();
        UUID eventId2 = UUID.randomUUID();

        testEvent1 = OutboxFeedEvent.builder()
                .id(eventId1)
                .aggregateType(AggregateType.POST)
                .aggregateId(1L)
                .eventType(EventType.POST_CREATED)
                .payload("{\"data\":\"payload1\"}")
                .processed(false)
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        testEvent2 = OutboxFeedEvent.builder()
                .id(eventId2)
                .aggregateType(AggregateType.LIKE)
                .aggregateId(2L)
                .eventType(EventType.LIKE_CREATED)
                .payload("{\"data\":\"payload2\"}")
                .processed(false)
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .build();
    }

    @Test
    void publishEventsProcessesAndSendsEvents() {
        int pageSize = 10;
        when(properties.getPageSize()).thenReturn(pageSize);
        Pageable pageable0 = PageRequest.of(0, pageSize);
        Pageable pageable1 = PageRequest.of(1, pageSize);

        List<OutboxFeedEvent> page0Content = new java.util.ArrayList<>();
        for(int i = 0; i < pageSize; i++) {
            page0Content.add(OutboxFeedEvent.builder().id(UUID.randomUUID()).aggregateType(AggregateType.POST).aggregateId((long)i).payload("{}").build());
        }
        when(outboxRepository.findUnprocessedEvents(pageable0)).thenReturn(page0Content);
        when(outboxRepository.findUnprocessedEvents(pageable1)).thenReturn(Collections.emptyList());

        CompletableFuture<SendResult<String, Object>> successfulFuture = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(successfulFuture);

        outboxEventPublisher.publishEvents();

        verify(outboxRepository, times(2)).findUnprocessedEvents(any(Pageable.class));
        verify(kafkaTemplate, times(pageSize)).send(anyString(), anyString(), anyString());
        verify(outboxRepository, times(pageSize)).save(any(OutboxFeedEvent.class));
    }

    @Test
    void publishEventsHandlesMultipleEventPages() {
        when(properties.getPageSize()).thenReturn(1);
        Pageable pageable0 = PageRequest.of(0, 1);
        Pageable pageable1 = PageRequest.of(1, 1);
        Pageable pageable2 = PageRequest.of(2, 1);

        when(outboxRepository.findUnprocessedEvents(pageable0)).thenReturn(List.of(testEvent1));
        when(outboxRepository.findUnprocessedEvents(pageable1)).thenReturn(List.of(testEvent2));
        when(outboxRepository.findUnprocessedEvents(pageable2)).thenReturn(Collections.emptyList());

        CompletableFuture<SendResult<String, Object>> successfulFuture = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(successfulFuture);

        outboxEventPublisher.publishEvents();

        verify(kafkaTemplate, times(2)).send(anyString(), anyString(), anyString());
        verify(outboxRepository, times(2)).save(any(OutboxFeedEvent.class));
        verify(outboxRepository, times(3)).findUnprocessedEvents(any(Pageable.class));
    }


    @Test
    void publishEventsWithNoEventsToPublish() {
        when(properties.getPageSize()).thenReturn(10);
        Pageable pageable0 = PageRequest.of(0, 10);
        when(outboxRepository.findUnprocessedEvents(pageable0)).thenReturn(Collections.emptyList());

        outboxEventPublisher.publishEvents();

        verify(outboxRepository).findUnprocessedEvents(pageable0);
        verifyNoInteractions(kafkaTemplate);
        verify(outboxRepository, never()).save(any(OutboxFeedEvent.class));
    }

    @Test
    void publishEventsWithKafkaSendFailureLogsAndDoesNotMarkAsProcessed() {
        when(properties.getPageSize()).thenReturn(10);
        Pageable pageable0 = PageRequest.of(0, 10);
        when(outboxRepository.findUnprocessedEvents(pageable0)).thenReturn(List.of(testEvent1));

        CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka send failed"));
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(failedFuture);

        outboxEventPublisher.publishEvents();

        verify(kafkaTemplate).send(eq(testEvent1.getAggregateType().getTopic()), eq("1"), eq(testEvent1.getPayload()));
        verify(outboxRepository, never()).save(eventCaptor.capture());
    }

    @Test
    void publishEventsWithMarkAsProcessedFailureLogs() {
        when(properties.getPageSize()).thenReturn(10);
        Pageable pageable0 = PageRequest.of(0, 10);
        when(outboxRepository.findUnprocessedEvents(pageable0)).thenReturn(List.of(testEvent1));

        CompletableFuture<SendResult<String, Object>> successfulFuture = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), anyString(), anyString())).thenReturn(successfulFuture);

        doThrow(new RuntimeException("DB error on save")).when(outboxRepository).save(any(OutboxFeedEvent.class));

        outboxEventPublisher.publishEvents();

        verify(kafkaTemplate).send(eq(testEvent1.getAggregateType().getTopic()), eq("1"), eq(testEvent1.getPayload()));
        verify(outboxRepository).save(eventCaptor.capture());
        OutboxFeedEvent captured = eventCaptor.getValue();
        assertTrue(captured.isProcessed());
    }


    @Test
    void cleanupProcessedEventsDeletesOldEvents() {
        int thresholdDays = 7;
        when(properties.getThresholdDays()).thenReturn(thresholdDays);
        int deletedCount = 5;
        when(outboxRepository.deleteOldProcessed(any(LocalDateTime.class))).thenReturn(deletedCount);

        outboxEventPublisher.cleanupProcessedEvents();

        verify(outboxRepository).deleteOldProcessed(dateTimeCaptor.capture());
        LocalDateTime capturedThreshold = dateTimeCaptor.getValue();
        assertTrue(capturedThreshold.isBefore(LocalDateTime.now().minusDays(thresholdDays).plusSeconds(1)));
        assertTrue(capturedThreshold.isAfter(LocalDateTime.now().minusDays(thresholdDays).minusSeconds(1)));
    }

    @Test
    void cleanupProcessedEventsWithNoEventsDeleted() {
        int thresholdDays = 7;
        when(properties.getThresholdDays()).thenReturn(thresholdDays);
        when(outboxRepository.deleteOldProcessed(any(LocalDateTime.class))).thenReturn(0);

        outboxEventPublisher.cleanupProcessedEvents();

        verify(outboxRepository).deleteOldProcessed(any(LocalDateTime.class));
    }
}

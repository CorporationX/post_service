package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.CommentEvent;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentEventPublisherImpl Tests")
class CommentEventPublisherImplTest {

    @Mock
    private KafkaTemplate<String, CommentEvent> kafkaTemplate;

    @InjectMocks
    private CommentEventPublisherImpl commentEventPublisher;

    @Captor
    private ArgumentCaptor<String> topicCaptor;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<CommentEvent> eventCaptor;

    private static final String TOPIC_NAME = "comment-events";
    private static final Long POST_ID = 10L;
    private static final Long COMMENT_ID = 100L;
    private static final Long COMMENT_AUTHOR_ID = 1L;
    private static final Long POST_AUTHOR_ID = 2L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(commentEventPublisher, "topic", TOPIC_NAME);
    }

    @Test
    @DisplayName("Should publish comment event successfully")
    void publish_WithValidEvent_ShouldSendToKafka() {
        // Arrange
        CommentEvent event = CommentEvent.builder()
                .commentId(COMMENT_ID)
                .commentAuthorId(COMMENT_AUTHOR_ID)
                .postAuthorId(POST_AUTHOR_ID)
                .postId(POST_ID)
                .commentText("Test comment")
                .createdAt(LocalDateTime.now())
                .build();

        TopicPartition topicPartition = new TopicPartition(TOPIC_NAME, 0);
        RecordMetadata recordMetadata = new RecordMetadata(
                topicPartition,
                0L,
                0L,
                LocalDateTime.now().toEpochSecond(java.time.ZoneOffset.UTC),
                null,
                0,
                0
        );

        SendResult<String, CommentEvent> sendResult = new SendResult<>(
                null,
                recordMetadata
        );

        CompletableFuture<SendResult<String, CommentEvent>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any(CommentEvent.class))).thenReturn(future);

        // Act
        commentEventPublisher.publish(event);

        // Assert
        verify(kafkaTemplate, times(1)).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                eventCaptor.capture()
        );

        assertThat(topicCaptor.getValue()).isEqualTo(TOPIC_NAME);
        assertThat(keyCaptor.getValue()).isEqualTo(String.valueOf(POST_ID));
        assertThat(eventCaptor.getValue()).isEqualTo(event);
        assertThat(eventCaptor.getValue().getCommentId()).isEqualTo(COMMENT_ID);
        assertThat(eventCaptor.getValue().getPostId()).isEqualTo(POST_ID);
        assertThat(eventCaptor.getValue().getCommentText()).isEqualTo("Test comment");
    }

    @Test
    @DisplayName("Should handle Kafka send failure gracefully")
    void publish_WithKafkaFailure_ShouldHandleError() {
        // Arrange
        CommentEvent event = CommentEvent.builder()
                .commentId(COMMENT_ID)
                .commentAuthorId(COMMENT_AUTHOR_ID)
                .postAuthorId(POST_AUTHOR_ID)
                .postId(POST_ID)
                .commentText("Test comment")
                .createdAt(LocalDateTime.now())
                .build();

        RuntimeException kafkaException = new RuntimeException("Kafka send failed");
        CompletableFuture<SendResult<String, CommentEvent>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(kafkaException);

        when(kafkaTemplate.send(anyString(), anyString(), any(CommentEvent.class))).thenReturn(failedFuture);

        // Act
        commentEventPublisher.publish(event);

        // Assert
        verify(kafkaTemplate, times(1)).send(
                eq(TOPIC_NAME),
                eq(String.valueOf(POST_ID)),
                eq(event)
        );

        // Wait a bit for the async callback to execute
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DisplayName("Should use postId as Kafka key")
    void publish_ShouldUsePostIdAsKey() {
        // Arrange
        Long differentPostId = 20L;
        CommentEvent event = CommentEvent.builder()
                .commentId(COMMENT_ID)
                .postId(differentPostId)
                .commentText("Test comment")
                .createdAt(LocalDateTime.now())
                .build();

        TopicPartition topicPartition = new TopicPartition(TOPIC_NAME, 0);
        RecordMetadata recordMetadata = new RecordMetadata(
                topicPartition,
                0L,
                0L,
                LocalDateTime.now().toEpochSecond(java.time.ZoneOffset.UTC),
                null,
                0,
                0
        );

        SendResult<String, CommentEvent> sendResult = new SendResult<>(
                null,
                recordMetadata
        );

        CompletableFuture<SendResult<String, CommentEvent>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any(CommentEvent.class))).thenReturn(future);

        // Act
        commentEventPublisher.publish(event);

        // Assert
        verify(kafkaTemplate, times(1)).send(
                eq(TOPIC_NAME),
                eq(String.valueOf(differentPostId)),
                eq(event)
        );
    }

    @Test
    @DisplayName("Should publish event with all fields populated")
    void publish_WithAllFields_ShouldPublishCompleteEvent() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now();
        CommentEvent event = CommentEvent.builder()
                .commentId(COMMENT_ID)
                .commentAuthorId(COMMENT_AUTHOR_ID)
                .postAuthorId(POST_AUTHOR_ID)
                .postId(POST_ID)
                .commentText("Full comment text")
                .createdAt(createdAt)
                .build();

        TopicPartition topicPartition = new TopicPartition(TOPIC_NAME, 1);
        RecordMetadata recordMetadata = new RecordMetadata(
                topicPartition,
                100L,
                50L,
                createdAt.toEpochSecond(java.time.ZoneOffset.UTC),
                null,
                0,
                0
        );

        SendResult<String, CommentEvent> sendResult = new SendResult<>(
                null,
                recordMetadata
        );

        CompletableFuture<SendResult<String, CommentEvent>> future = CompletableFuture.completedFuture(sendResult);

        when(kafkaTemplate.send(anyString(), anyString(), any(CommentEvent.class))).thenReturn(future);

        // Act
        commentEventPublisher.publish(event);

        // Assert
        verify(kafkaTemplate, times(1)).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                eventCaptor.capture()
        );

        CommentEvent publishedEvent = eventCaptor.getValue();
        assertThat(publishedEvent.getCommentId()).isEqualTo(COMMENT_ID);
        assertThat(publishedEvent.getCommentAuthorId()).isEqualTo(COMMENT_AUTHOR_ID);
        assertThat(publishedEvent.getPostAuthorId()).isEqualTo(POST_AUTHOR_ID);
        assertThat(publishedEvent.getPostId()).isEqualTo(POST_ID);
        assertThat(publishedEvent.getCommentText()).isEqualTo("Full comment text");
        assertThat(publishedEvent.getCreatedAt()).isEqualTo(createdAt);
    }
}

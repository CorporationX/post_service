package faang.school.postservice.app.listener;

import faang.school.postservice.kafka.producer.LikeKafkaProducer;
import faang.school.postservice.model.enums.LikePostEvent;
import faang.school.postservice.model.event.application.LikeCommitedEvent;
import faang.school.postservice.model.event.kafka.LikeKafkaEvent;
import faang.school.postservice.redis.publisher.LikeEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeCommitedEventListenerTest {

    @Mock
    private LikeEventPublisher likeEventPublisher;

    @Mock
    private LikeKafkaProducer likeKafkaProducer;

    @InjectMocks
    private LikeCommitedEventListener listener;

    @Test
    void testHandleLikeCommittedEvent() {
        // Arrange
        Long likeId = 1L;
        Long postId = 2L;
        Long likeAuthorId = 3L;
        Long postAuthorId = 4L;

        LikeCommitedEvent event = new LikeCommitedEvent(likeId, likeAuthorId, postId, postAuthorId);

        // Act
        listener.handleLikeCommittedEvent(event);

        // Assert
        // Захватываем и проверяем LikePostEvent
        ArgumentCaptor<LikePostEvent> postEventCaptor = ArgumentCaptor.forClass(LikePostEvent.class);
        verify(likeEventPublisher, times(1)).publish(postEventCaptor.capture());
        LikePostEvent capturedPostEvent = postEventCaptor.getValue();
        assertEquals(likeAuthorId, capturedPostEvent.getLikeAuthorId());
        assertEquals(postId, capturedPostEvent.getPostId());
        assertEquals(postAuthorId, capturedPostEvent.getPostAuthorId());

        // Захватываем и проверяем LikeKafkaEvent
        ArgumentCaptor<LikeKafkaEvent> kafkaEventCaptor = ArgumentCaptor.forClass(LikeKafkaEvent.class);
        verify(likeKafkaProducer, times(1)).sendEvent(kafkaEventCaptor.capture());
        LikeKafkaEvent capturedKafkaEvent = kafkaEventCaptor.getValue();
        assertEquals(likeId, capturedKafkaEvent.getLikeId());
        assertEquals(postId, capturedKafkaEvent.getPostId());
    }
}

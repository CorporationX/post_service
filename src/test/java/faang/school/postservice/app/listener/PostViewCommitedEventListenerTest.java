package faang.school.postservice.app.listener;

import faang.school.postservice.kafka.producer.PostViewKafkaProducer;
import faang.school.postservice.model.event.PostViewEvent;
import faang.school.postservice.model.event.application.PostViewCommittedEvent;
import faang.school.postservice.model.event.kafka.PostViewKafkaEvent;
import faang.school.postservice.redis.publisher.PostViewPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostViewCommitedEventListenerTest {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Mock
    private PostViewPublisher postViewPublisher;

    @Mock
    private PostViewKafkaProducer postViewKafkaProducer;

    @InjectMocks
    private PostViewCommitedEventListener listener;

    @Test
    void testHandlePostViewCommittedEvent() {
        // Arrange
        Long postId = 1L;
        Long postAuthorId = 2L;
        Long viewerId = 3L;
        LocalDateTime now = LocalDateTime.now();

        PostViewCommittedEvent event = new PostViewCommittedEvent(postId, postAuthorId, viewerId);

        // Act
        listener.handlePostViewCommittedEvent(event);

        // Assert
        // Проверка вызова PostViewPublisher
        ArgumentCaptor<PostViewEvent> postViewEventCaptor = ArgumentCaptor.forClass(PostViewEvent.class);
        verify(postViewPublisher, times(1)).publish(postViewEventCaptor.capture());
        PostViewEvent capturedPostViewEvent = postViewEventCaptor.getValue();
        assertEquals(postId, capturedPostViewEvent.getPostId());
        assertEquals(postAuthorId, capturedPostViewEvent.getAuthorId());
        assertEquals(viewerId, capturedPostViewEvent.getActorId());

        // Проверка вызова PostViewKafkaProducer
        ArgumentCaptor<PostViewKafkaEvent> postViewKafkaEventCaptor = ArgumentCaptor.forClass(PostViewKafkaEvent.class);
        verify(postViewKafkaProducer, times(1)).sendEvent(postViewKafkaEventCaptor.capture());
        PostViewKafkaEvent capturedPostViewKafkaEvent = postViewKafkaEventCaptor.getValue();
        assertEquals(postId, capturedPostViewKafkaEvent.getPostId());
        assertEquals(viewerId, capturedPostViewKafkaEvent.getViewerId());
        assertEquals(now.format(formatter), capturedPostViewKafkaEvent.getViewDateTime());
    }
}

package faang.school.postservice.messaging.kafka.publishing;

import faang.school.postservice.messaging.kafka.events.CommentEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentProducerTest {

    @InjectMocks
    private CommentProducer commentProducer;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    public void testPublish() {
        CommentEvent commentEvent = CommentEvent.builder()
                .id(1L)
                .postId(1L)
                .build();
        String commentEventChannel = "comment_event_channel";
        commentProducer.setCommentEventChannel(commentEventChannel);
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(commentEventChannel, commentEvent)).thenAnswer(invocation -> future);

        commentProducer.publish(commentEvent);

        verify(kafkaTemplate, times(1)).send(commentEventChannel, commentEvent);
    }
}

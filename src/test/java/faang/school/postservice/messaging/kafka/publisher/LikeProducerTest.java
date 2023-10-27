package faang.school.postservice.messaging.kafka.publisher;

import faang.school.postservice.messaging.kafka.events.LikeEvent;
import faang.school.postservice.messaging.kafka.publishing.like.LikeProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class LikeProducerTest {
    private final String topic = "like_event_channel";

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks
    private LikeProducer likeProducer;

    @Test
    public void shouldPublishEvent() {
        LikeEvent event = LikeEvent.builder()
                .id(0L)
                .postId(2L)
                .build();
        likeProducer.setTopic(topic);
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(any(String.class), any(Object.class)))
                .thenAnswer(invocation -> future);
        likeProducer.publish(event);

        verify(kafkaTemplate, times(1))
                .send(topic, event);
    }
}

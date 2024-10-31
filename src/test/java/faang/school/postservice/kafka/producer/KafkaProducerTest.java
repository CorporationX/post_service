package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.event.Event;
import faang.school.postservice.kafka.event.post.PostViewedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaProducerTest {
    @InjectMocks
    private KafkaProducer kafkaProducer;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void testSend() {
        String topic = "testTopic";
        Event event = new PostViewedEvent(1L, 100L);
        kafkaProducer.send(topic, event);
        verify(kafkaTemplate).send(topic, event);
    }
}
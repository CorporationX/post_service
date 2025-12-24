package faang.school.postservice.publisher;

import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.kafka.KafkaLikeProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaLikeProducerTest {
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaLikeProducer kafkaLikeProducer;

    @Test
    void publishToKafka_shouldSendMessageToKafka() {

        LikeEvent event = new LikeEvent(1L, 2L, 3L, LocalDateTime.now());

        ReflectionTestUtils.setField(kafkaLikeProducer, "topicName", "like");

        kafkaLikeProducer.publishToKafka(event);

        verify(kafkaTemplate, times(1))
                .send("like", event);
    }
}

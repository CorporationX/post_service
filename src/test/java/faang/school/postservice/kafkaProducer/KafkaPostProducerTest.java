package faang.school.postservice.kafkaProducer;

import faang.school.postservice.dto.kafkaEvents.PostCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.mockito.Mockito.*;

public class KafkaPostProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaPostProducer kafkaPostProducer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendPostCreatedEvent() {
        PostCreatedEvent event = new PostCreatedEvent(1L, 1L, List.of(2L, 3L));

        kafkaPostProducer.sendPostCreatedEvent(event);

        verify(kafkaTemplate, times(1)).send("posts", event);
    }
}

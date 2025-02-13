package faang.school.postservice.producer;

import faang.school.postservice.event.PostEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaPostProducerTest {
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaPostProducer kafkaPostProducer;

    private final String topicName = "post-topic";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaPostProducer, "topicName", topicName);
    }

    @Test
    void testSendPostEvent() {
        PostEvent postEvent = new PostEvent( UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),1L,2L, List.of(1L,2L));

        kafkaPostProducer.sendPostEvent(postEvent);

        verify(kafkaTemplate, times(1)).send(topicName, postEvent);
    }

}
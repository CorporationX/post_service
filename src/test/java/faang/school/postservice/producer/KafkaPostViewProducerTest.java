package faang.school.postservice.producer;

import faang.school.postservice.event.like.PostViewEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaPostViewProducerTest {

    @InjectMocks
    private KafkaPostViewProducer kafkaPostViewProducer;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private NewTopic postViewTopic;

    @Test
    @DisplayName("When send message in kafka then success")
    public void whenPublishEventShouldSuccess() {
        PostViewEvent event = PostViewEvent.builder().build();

        kafkaPostViewProducer.sendMessage(event);

        verify(kafkaTemplate).send(any(), any(PostViewEvent.class));
    }
}
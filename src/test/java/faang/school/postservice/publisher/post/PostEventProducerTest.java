package faang.school.postservice.publisher.post;

import faang.school.postservice.dto.event.PostFeedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostEventProducerTest {

    @InjectMocks
    PostEventProducer postEventProducer;

    @Mock
    NewTopic postTopic;
    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void testSendEvent_successfulCompletion() {
        PostFeedEvent event = new PostFeedEvent();
        when(postTopic.name()).thenReturn("post-topic");
        when(kafkaTemplate.send("post-topic", event)).thenReturn(null);

        postEventProducer.sendEvent(event);

        verify(kafkaTemplate).send("post-topic", event);
    }
}
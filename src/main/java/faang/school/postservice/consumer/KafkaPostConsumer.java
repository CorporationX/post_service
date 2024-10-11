package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.kafka.PostCreatedEvent;
import org.springframework.kafka.support.Acknowledgment;

public class KafkaPostConsumer implements KafkaConsumer<PostCreatedEvent> {
    @Override
    public void onMessage(PostCreatedEvent event, Acknowledgment acknowledgment) {

    }
}

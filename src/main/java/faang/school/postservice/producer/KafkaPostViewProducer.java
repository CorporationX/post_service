package faang.school.postservice.producer;

import faang.school.postservice.dto.kafka.event.PostViewEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewProducer extends AbstractKafkaProducer<PostViewEventDto> {
    public KafkaPostViewProducer(
        KafkaTemplate<String, String> kafkaTemplate,
        @Qualifier("postViewKafkaTopic")
        NewTopic topic
    ) {
        super(kafkaTemplate, topic);
    }
}

package faang.school.postservice.producer;

import faang.school.postservice.dto.kafka.event.PostEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractKafkaProducer<PostEventDto> {
    public KafkaPostProducer(
        KafkaTemplate<String, PostEventDto> kafkaTemplate,

        @Qualifier("postKafkaTopic")
        NewTopic topic) {
        super(kafkaTemplate, topic);
    }
}

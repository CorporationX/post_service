package faang.school.postservice.producer;

import faang.school.postservice.dto.kafka.event.CommentEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer extends AbstractKafkaProducer<CommentEventDto> {
    public KafkaCommentProducer(
        KafkaTemplate<String, String> kafkaTemplate,

        @Qualifier("commentKafkaTopic")
        NewTopic topic
    ) {
        super(kafkaTemplate, topic);
    }
}

package faang.school.postservice.producer;

import faang.school.postservice.dto.kafka.event.LikeEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends AbstractKafkaProducer<LikeEventDto> {
    public KafkaLikeProducer(
        KafkaTemplate<String, String> kafkaTemplate,

        @Qualifier("likeKafkaTopic")
        NewTopic topic
    ) {
        super(kafkaTemplate, topic);
    }
}

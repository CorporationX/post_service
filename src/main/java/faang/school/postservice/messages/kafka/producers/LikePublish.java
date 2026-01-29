package faang.school.postservice.messages.kafka.producers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikePublish extends AbstractPublishKafka {

    public LikePublish(@Qualifier("kafkaTemplate")
                          KafkaTemplate<String, Object> kafkaTemplate,
                          @Value("${spring.kafka.topics.like}")
                          String likeTopic) {
        super(kafkaTemplate, likeTopic);
    }
}

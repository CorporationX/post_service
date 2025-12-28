package faang.school.postservice.messages.kafka.producers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentPublish extends AbstractPublishKafka {

    public CommentPublish(@Qualifier("kafkaTemplate")
                          KafkaTemplate<String, Object> kafkaTemplate,
                          @Value("${spring.kafka.topics.comment}")
                          String commentTopic) {
        super(kafkaTemplate, commentTopic);
    }
}
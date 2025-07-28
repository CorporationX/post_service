package faang.school.postservice.kafka.producer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class KafkaTopics {
    @Value("${spring.kafka.topics.comment-created.name}")
    private String commentCreatedTopic;
}

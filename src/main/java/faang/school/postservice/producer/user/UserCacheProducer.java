package faang.school.postservice.producer.user;

import faang.school.postservice.event.user.UserCacheEvent;
import faang.school.postservice.producer.AbstractProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserCacheProducer extends AbstractProducer<UserCacheEvent> {
    public UserCacheProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${kafka.topic.user-cache-topic.name}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}

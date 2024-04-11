package faang.school.postservice.publisher.kafka_producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPostViewProducer {
    @Value("${spring.kafka.topics.post_view.name}")
    private String postViewTopic;

    public void publishPostViewKafkaEvent(Long userId, Long postId) {

    }
}

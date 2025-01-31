package faang.school.postservice.producer;

import faang.school.postservice.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewProducer implements KafkaPublisher<PostViewEvent>{

    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final NewTopic postViewKafkaTopic;

    @Override
    public void publish(PostViewEvent event) {
        kafkaTemplate.send(postViewKafkaTopic.name(), event);
        log.info("Post view event was sent to Kafka topic {}: {}", postViewKafkaTopic.name(), event);
    }
}

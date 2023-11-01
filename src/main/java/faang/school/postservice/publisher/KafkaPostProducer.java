package faang.school.postservice.publisher;

import faang.school.postservice.dto.kafka.KafkaPostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${spring.data.kafka.topics.post}")
    private String postsTopic;

    public void publish(KafkaPostEvent event){
        kafkaTemplate.send(postsTopic, event);

        log.info("Post event was published to kafka with post ID: {}, and amount of followers: {}", event.getPostPair().postId(), event.getFollowersIds().size());
    }
}

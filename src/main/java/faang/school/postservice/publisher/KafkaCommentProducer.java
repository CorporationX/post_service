package faang.school.postservice.publisher;

import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${spring.data.kafka.topics.comments}")
    private String commentsTopic;

    public void publish(KafkaCommentEvent event){
        kafkaTemplate.send(commentsTopic, event);

        log.info("Comment event was published to kafka with post ID: {}, and comment ID: {}", event.getPostId(), event.getCommentDto().getId());
    }
}

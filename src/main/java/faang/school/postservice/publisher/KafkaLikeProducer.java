package faang.school.postservice.publisher;

import faang.school.postservice.dto.kafka.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${spring.data.kafka.topics.likes.name}")
    private String likesTopic;

    public void publish(LikeEvent event){
        kafkaTemplate.send(likesTopic, event);
    }
}

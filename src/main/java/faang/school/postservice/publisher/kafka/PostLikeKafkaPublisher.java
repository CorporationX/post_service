package faang.school.postservice.publisher.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostLikeEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostLikeKafkaPublisher extends AbstractPublisher<PostLikeEventDto> {

    public PostLikeKafkaPublisher(
            @Value("${spring.kafka.topic.like-post}") String likePostTopic,
                KafkaTemplate<String, String> kafkaTemplate,
                ObjectMapper objectMapper) {
        super(likePostTopic, kafkaTemplate, objectMapper);
    }

    public void publish(PostLikeEventDto event) {
        super.publish(event);
    }
}

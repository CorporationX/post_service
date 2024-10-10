package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.KafkaPostDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostProducer implements EventPublisher<KafkaPostDto>{

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NewTopic topicPost;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(KafkaPostDto event) {
        try {
            String message =  objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topicPost.name(), message);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
}

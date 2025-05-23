package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaProperties kafkaProperties;

    public void sendCommentEvent(CommentEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            String topic = kafkaProperties.getTopics().getComments();
            kafkaTemplate.send(topic, json);
            log.info("CommentEvent для поста {}", event.getPostId());
        } catch (Exception e) {
            log.error("❌ Не удалось отправить CommentEvent", e);
        }
    }
}

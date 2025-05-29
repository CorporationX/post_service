package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.CommentEvent;
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

        final Long postId = event.getPostId();

        try {
            String json = objectMapper.writeValueAsString(event);
            String topic = kafkaProperties.getTopics().getComments();

            kafkaTemplate.send(topic, json)
                    .thenAccept(result ->
                            log.info("Successfully sent CommentEvent for post {}", postId)
                    )
                    .exceptionally(ex -> {
                        log.error("Failed to send CommentEvent for post {}", postId, ex);
                        return null;
                    });

        } catch (Exception e) {
            log.error("Failed to serialize CommentEvent for post {}", postId, e);
        }
    }
}

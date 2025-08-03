package faang.school.postservice.publisher.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentProducer implements MessagePublisher<CommentEvent> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(CommentEvent comment) {
        if (!kafkaProperties.active()) {
            return;
        }

        try {
            kafkaTemplate.send(kafkaProperties.topicNames().comments(), objectMapper.writeValueAsString(comment));
            log.info("Message published in kafka. Comment {}", comment);
        } catch (JsonProcessingException e) {
            log.error("Message not published in kafka. Can't convert comment event to json [{}]: {}", comment, e.getMessage(), e);
        }
    }
}

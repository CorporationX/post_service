package faang.school.postservice.publisher.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeProducer implements MessagePublisher<LikeEvent> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(LikeEvent likeEvent) {
        if (!kafkaProperties.active()) {
            return;
        }

        try {
            kafkaTemplate.send(kafkaProperties.topicNames().likes(), objectMapper.writeValueAsString(likeEvent));
            log.info("Message published in kafka. Like [{}]", likeEvent);
        } catch (JsonProcessingException e) {
            log.error("Message not published in kafka. Can't convert like event to json [{}]: {}", likeEvent, e.getMessage(), e);
        }
    }
}

package faang.school.postservice.publisher.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewProducer implements MessagePublisher<PostViewEvent> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(PostViewEvent postViewEvent) {
        if (!kafkaProperties.active()) {
            return;
        }

        try {
            kafkaTemplate.send(kafkaProperties.topicNames().postView(), objectMapper.writeValueAsString(postViewEvent));
            log.info("Message published in kafka. Viewed post [{}]", postViewEvent);
        } catch (JsonProcessingException e) {
            log.error("Message not published in kafka. Can't convert post view event to json [{}]", postViewEvent, e);
        }
    }
}

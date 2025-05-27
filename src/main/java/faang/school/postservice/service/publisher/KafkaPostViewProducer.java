package faang.school.postservice.service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.AbstractKafkaProducer;
import faang.school.postservice.event.PostViewEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPostViewProducer extends AbstractKafkaProducer {

    @Value("${spring.kafka.producer.topics.post-view")
    private String postViewTopic;

    public KafkaPostViewProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        super(kafkaTemplate, objectMapper);
    }

    public void sendPostViewEvent(
            PostViewEvent postViewEvent
    ) {
        log.info("Sending post view event: {}", postViewEvent);
        send(postViewTopic, postViewEvent);
    }
}

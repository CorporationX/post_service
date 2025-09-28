package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperty;
import faang.school.postservice.dto.event.LikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LikeProducer extends KafkaProducer<LikeEvent> {
    private final KafkaProperty kafkaProps;

    public LikeProducer(ObjectMapper objectMapper,
                        KafkaProperty kafkaProperty,
                        KafkaTemplate<String, String> kafkaTemplate) {
        super(objectMapper, kafkaTemplate);
        this.kafkaProps = kafkaProperty;
    }

    @Override
    public void sendEvent(LikeEvent event) {
        log.info("Sending event of like post: likeId = {}, postId = {}", event.likeId(), event.postId());
        sendTo(kafkaProps.topic().postLike(), event);
    }
}

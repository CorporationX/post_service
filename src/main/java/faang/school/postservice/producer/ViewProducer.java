package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperty;
import faang.school.postservice.dto.event.ViewEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ViewProducer extends KafkaProducer<ViewEvent> {
    private final KafkaProperty kafkaProps;

    public ViewProducer(ObjectMapper objectMapper,
                        KafkaProperty kafkaProps,
                        KafkaTemplate<String, String> kafkaTemplate) {
        super(objectMapper, kafkaTemplate);
        this.kafkaProps = kafkaProps;
    }

    @Override
    public void sendEvent(ViewEvent event) {
        log.info("Sending event of post view: viewId = {}, postId = {}", event.viewId(), event.postId());
        sendTo(kafkaProps.topic().postView(), event);
    }
}

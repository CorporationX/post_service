package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.NewPostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaPostProducer extends AbstractProducer<NewPostEvent> {

    @Value("${spring.kafka.topic.new-post}")
    private String topicName;

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void sendMessage(NewPostEvent event) {
        sendMessage(topicName, event);
    }
}

package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.PostKafkaEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractEventProducer<PostKafkaEventDto> {
    @Value("${spring.kafka.topics.post.name}")
    private String topicName;

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Async("executorService")
    public void sendAsyncPostEvent(PostKafkaEventDto event) {
        sendEvent(event, topicName);
    }
}
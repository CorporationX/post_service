package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.event.EventDto;
import faang.school.postservice.dto.event.PostViewEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewEventProducer extends AbstractEventProducer {

    public KafkaPostViewEventProducer(
            KafkaTemplate<String, EventDto> kafkaTemplate,
            NewTopic commentKafkaTopic) {
        super(kafkaTemplate, commentKafkaTopic);
    }

    @Async
    @Retryable
    public void handleNewPostView(PostViewEventDto viewEvent) {
        sendEvent(viewEvent, String.valueOf(viewEvent.getPostId()));
    }
}

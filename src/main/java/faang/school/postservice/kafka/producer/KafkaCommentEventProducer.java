package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.dto.event.EventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentEventProducer extends AbstractEventProducer {

    public KafkaCommentEventProducer(
            KafkaTemplate<String, EventDto> kafkaTemplate,
            NewTopic commentKafkaTopic
    ) {
        super(kafkaTemplate, commentKafkaTopic);
    }

    @Async
    @Retryable
    public void sendCommentEvent(CommentEventDto commentEventDto) {
        sendEvent(commentEventDto, String.valueOf(commentEventDto.getCommentAuthorId()));
    }
}

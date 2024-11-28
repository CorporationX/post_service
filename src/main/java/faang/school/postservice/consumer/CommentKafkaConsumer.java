package faang.school.postservice.consumer;

import faang.school.postservice.dto.kafka.event.CommentEventDto;
import faang.school.postservice.service.kafka.KafkaReceivedEventService;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CommentKafkaConsumer extends AbstractKafkaConsumer<CommentEventDto, KafkaReceivedEventService<CommentEventDto>> {

    public CommentKafkaConsumer(
        KafkaReceivedEventService<CommentEventDto> service,
        @Qualifier("commentKafkaTopic") NewTopic topic
    ) {
        super(service, topic);
    }

    @KafkaListener(
        id = "consumer-comments",
        topics = "${spring.data.kafka.topic.comment-topic.name}",
        containerFactory = "kafkaCommentConsumerFactory"
    )
    @Override
    public void listen(CommentEventDto event) {
        process(event);
    }
}

package faang.school.postservice.consumer;

import faang.school.postservice.dto.kafka.event.PostViewEventDto;
import faang.school.postservice.service.kafka.KafkaReceivedEventService;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PostViewKafkaConsumer extends AbstractKafkaConsumer<PostViewEventDto, KafkaReceivedEventService<PostViewEventDto>> {
    public PostViewKafkaConsumer(
        KafkaReceivedEventService<PostViewEventDto> service,
        @Qualifier("postViewKafkaTopic") NewTopic topic
    ) {
        super(service, topic);
    }

    @Override
    @KafkaListener(
        id = "consumer-post-views",
        topics = "${spring.data.kafka.topic.post-view-topic.name}",
        containerFactory = "kafkaPostConsumerFactory"
    )
    public void listen(PostViewEventDto event) {
        process(event);
    }
}

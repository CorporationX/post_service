package faang.school.postservice.consumer;

import faang.school.postservice.dto.kafka.event.LikeEventDto;
import faang.school.postservice.service.kafka.KafkaReceivedEventService;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LikeKafkaConsumer extends AbstractKafkaConsumer<LikeEventDto, KafkaReceivedEventService<LikeEventDto>>  {
    public LikeKafkaConsumer(
        KafkaReceivedEventService<LikeEventDto> service,
        @Qualifier("likeKafkaTopic") NewTopic topic
    ) {
        super(service, topic);
    }

    @Override
    @KafkaListener(
        id = "consumer-likes",
        topics = "${spring.data.kafka.topic.like-topic.name}",
        containerFactory = "kafkaLikeConsumerFactory"
    )
    public void listen(LikeEventDto event) {
        process(event);
    }
}

package faang.school.postservice.consumer;

import faang.school.postservice.dto.kafka.event.PostEventDto;
import faang.school.postservice.service.kafka.KafkaReceivedEventService;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostKafkaConsumer
    extends AbstractKafkaConsumer<PostEventDto, KafkaReceivedEventService<PostEventDto>> {
    public PostKafkaConsumer(
        KafkaReceivedEventService<PostEventDto> service,
        @Qualifier("postKafkaTopic") NewTopic topic
    ) {
        super(service, topic);
    }

    @KafkaListener(
        id = "consumer-posts",
        //topics = "${spring.data.kafka.topic.post-topic.name}",
        topics = "posts",
        containerFactory = "kafkaPostConsumerFactory"
        //groupId = "posts"
    )
    @Override
    public void listen(PostEventDto event) {
        process(event);
    }
}

package faang.school.postservice.producer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.producer.AbstractEventProducer;
import faang.school.postservice.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PostEventProducer extends AbstractEventProducer<PostEventDto> {

    public PostEventProducer(
        @Value("${spring.kafka.topics.postTopic}") String topic,
        KafkaProducer kafkaProducer,
        ObjectMapper objectMapper
    ) {
        super(topic, kafkaProducer, objectMapper);
    }
}

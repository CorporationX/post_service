package faang.school.postservice.producer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.producer.AbstractEventProducer;
import faang.school.postservice.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LikeEventProducer extends AbstractEventProducer<LikeEventDto> {

    public LikeEventProducer(
        @Value("${spring.kafka.topics.likeTopic}") String topic,
        KafkaProducer kafkaProducer,
        ObjectMapper objectMapper
    ) {
        super(topic, kafkaProducer, objectMapper);
    }
}

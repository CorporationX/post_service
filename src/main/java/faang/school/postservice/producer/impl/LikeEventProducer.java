package faang.school.postservice.producer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.producer.AbstractEventProducer;
import faang.school.postservice.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LikeEventProducer extends AbstractEventProducer<LikeEventDto> {

    @Value("${spring.kafka.topics.likeTopic}")
    private String topic;

    public LikeEventProducer(KafkaProducer kafkaProducer, ObjectMapper objectMapper) {
        super(kafkaProducer, objectMapper);
    }

    @Override
    public String getTopic() {
        return topic;
    }
}

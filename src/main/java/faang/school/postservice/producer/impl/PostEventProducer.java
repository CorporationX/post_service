package faang.school.postservice.producer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.producer.AbstractEventProducer;
import faang.school.postservice.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PostEventProducer extends AbstractEventProducer<PostEventDto> {

    @Value("${spring.kafka.topics.postTopic}")
    private String topic;

    public PostEventProducer(KafkaProducer kafkaProducer, ObjectMapper objectMapper) {
        super(kafkaProducer, objectMapper);
    }

    @Override
    public String getTopic() {
        return topic;
    }
}

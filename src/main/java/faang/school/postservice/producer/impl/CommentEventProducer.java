package faang.school.postservice.producer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.producer.AbstractEventProducer;
import faang.school.postservice.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CommentEventProducer extends AbstractEventProducer<CommentEventDto> {

    @Value("${spring.kafka.topics.commentTopic}")
    private String topic;

    public CommentEventProducer(KafkaProducer kafkaProducer, ObjectMapper objectMapper) {
        super(kafkaProducer, objectMapper);
    }

    @Override
    public String getTopic() {
        return topic;
    }
}

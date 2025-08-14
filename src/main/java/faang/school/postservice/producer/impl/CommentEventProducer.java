package faang.school.postservice.producer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.producer.AbstractEventProducer;
import faang.school.postservice.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommentEventProducer extends AbstractEventProducer<CommentEventDto> {

    public CommentEventProducer(
        @Value("${spring.kafka.topics.commentTopic}") String topic,
        KafkaProducer kafkaProducer,
        ObjectMapper objectMapper
    ) {
        super(topic, kafkaProducer, objectMapper);
    }
}

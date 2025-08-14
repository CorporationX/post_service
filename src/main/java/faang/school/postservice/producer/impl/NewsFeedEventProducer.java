package faang.school.postservice.producer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.NewsFeedEventDto;
import faang.school.postservice.producer.AbstractEventProducer;
import faang.school.postservice.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NewsFeedEventProducer extends AbstractEventProducer<NewsFeedEventDto> {

    public NewsFeedEventProducer(
        @Value("${spring.kafka.topics.newsFeedTopic}") String topic,
        KafkaProducer kafkaProducer,
        ObjectMapper objectMapper
    ) {
        super(topic, kafkaProducer, objectMapper);
    }
}

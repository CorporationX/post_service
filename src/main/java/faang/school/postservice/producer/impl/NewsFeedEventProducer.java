package faang.school.postservice.producer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.NewsFeedEventDto;
import faang.school.postservice.producer.AbstractEventProducer;
import faang.school.postservice.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NewsFeedEventProducer extends AbstractEventProducer<NewsFeedEventDto> {

    @Value("${spring.kafka.topics.newsFeedTopic}")
    private String topic;

    public NewsFeedEventProducer(KafkaProducer kafkaProducer, ObjectMapper objectMapper) {
        super(kafkaProducer, objectMapper);
    }

    @Override
    public String getTopic() {
        return topic;
    }
}

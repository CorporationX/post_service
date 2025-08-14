package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractEventProducer<T> implements EventProducer<T> {

    private final String topic;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    public AbstractEventProducer(
        String topic, KafkaProducer kafkaProducer, ObjectMapper objectMapper
    ) {
        this.topic = topic;
        this.kafkaProducer = kafkaProducer;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(T eventDto) {
        try {
            String jsonText = objectMapper.writeValueAsString(eventDto);
            kafkaProducer.sendMessage(topic, jsonText);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

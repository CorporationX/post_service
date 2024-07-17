package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.kafka.KafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<T extends KafkaEvent> implements KafkaProducer<T> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, NewTopic> topicMap;

    @Override
    public void produce(T event) {

        String message = processEvent(event);
        
        NewTopic newCommentTopic = topicMap.get(getTopic());
        kafkaTemplate.send(newCommentTopic.name(), message);
        log.info("Published new event to Kafka - {}: {}", newCommentTopic.name(), message);
    }

    @Override
    public void produce(T event, Runnable runnable) {

        String message = processEvent(event);

        NewTopic newCommentTopic = topicMap.get(getTopic());
        kafkaTemplate.send(newCommentTopic.name(), message).thenRun(runnable);
        log.info("Published new event to Kafka with a runnable task - {}: {}", newCommentTopic.name(), message);
    }

    public abstract String getTopic();

    private String processEvent(T event) {

        String message;
        try {
            message = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing event", e);
        }
        return message;
    }
}

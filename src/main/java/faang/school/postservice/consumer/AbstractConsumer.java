package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractConsumer<E> {

    protected final ObjectMapper objectMapper;

    protected final Class<E> eventType;

    protected final String topicName;
    protected final String groupId;

    protected abstract void processEvent(E event);

    @KafkaListener(topics = "#{@getTopicName}", groupId = "#{@getGroupId}")
    public void consume(String message) throws JsonProcessingException {
        E event = objectMapper.readValue(message, eventType);
        log.info("Consumed message: {}", event.toString());
        processEvent(event);
    }

    protected abstract String getTopicName();

    protected abstract String getGroupId();
}

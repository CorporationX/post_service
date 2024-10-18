package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Retryable;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaConsumer<K, V> {
    private final String topicName;
    private final String groupId;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "#{__listener.topicName}", groupId = "#{__listener.groupId}")
    @Retryable(retryFor = {JsonProcessingException.class})
    public void consume(ConsumerRecord<K, String> record, Acknowledgment acknowledgment) {
        try {
            log.info("Consumed message from topic {}: {}", topicName, record.value());
            V message = objectMapper.readValue(record.value(), getValueType());
            processMessage(record.key(), message);
            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Json error processing message: {}", record.value(), e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Unexpected error processing message: {}", record.value(), e);
            throw new RuntimeException(e);
        }

    }

    protected abstract void processMessage(K key, V message);

    protected abstract Class<V> getValueType();
}

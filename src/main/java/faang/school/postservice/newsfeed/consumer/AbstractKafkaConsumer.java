package faang.school.postservice.newsfeed.consumer;

import faang.school.postservice.service.newsfeed.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
public abstract class AbstractKafkaConsumer<T> {

    protected final RedisCacheService redisCacheService;

    protected AbstractKafkaConsumer(RedisCacheService redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    @KafkaListener(topics = "#{__listener.getTopic()}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(ConsumerRecord<String, T> record) {
        try {
            T event = record.value();
            if (shouldSkip(record)) return;
            processEvent(event);
            markAsProcessed(record);
        } catch (Exception e) {
            log.error("Kafka Consumer failed", e);
            throw e;
        }
    }

    protected boolean shouldSkip(ConsumerRecord<String, T> record) {
        UUID eventId = generateEventId(record);
        return redisCacheService.isAlreadyProcessed(eventId);
    }

    protected void markAsProcessed(ConsumerRecord<String, T> record) {
        redisCacheService.markAsProcessed(generateEventId(record));
    }

    protected UUID generateEventId(ConsumerRecord<String, T> record) {
        String source = record.topic() + record.partition() + record.offset();
        return UUID.nameUUIDFromBytes(source.getBytes(StandardCharsets.UTF_8));
    }

    protected abstract void processEvent(T event);

    public abstract String getTopic();
}

package faang.school.postservice.listener.kafka;

import faang.school.postservice.dto.kafka.KafkaKey;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public abstract class AbstractKafkaListener<Event> {

    public void consume(ConsumerRecord<String, Object> message, Class<Event> type, BiConsumer<Event, KafkaKey> biConsumer) {
        KafkaKey kafkaKey = getKafkaKey(message);
        Event event = getEvent(message, type);

        biConsumer.accept(event, kafkaKey);
    }

    protected KafkaKey getKafkaKey(ConsumerRecord<String, Object> message) {
        return Enum.valueOf(KafkaKey.class, message.key());
    }

    protected Event getEvent(ConsumerRecord<String, Object> message, Class<Event> type) {
        return type.cast(message.value());
    }
}

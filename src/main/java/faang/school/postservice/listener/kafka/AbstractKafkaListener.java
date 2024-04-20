package faang.school.postservice.listener.kafka;

import faang.school.postservice.dto.kafka.KafkaKey;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
public abstract class AbstractKafkaListener<Event> {

    protected void consume(ConsumerRecord<String, Object> message, Class<Event> type, BiConsumer<Event, KafkaKey> biConsumer) {
        KafkaKey kafkaKey = Enum.valueOf(KafkaKey.class, message.key());
        Event event = type.cast(message.value());

        biConsumer.accept(event, kafkaKey);
    }
}

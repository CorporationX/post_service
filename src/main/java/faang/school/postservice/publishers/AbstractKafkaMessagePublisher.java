package faang.school.postservice.publishers;

import faang.school.postservice.events.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractKafkaMessagePublisher<T, E extends Event> implements MessagePublisher<T> {
    @Value("${kafka.header_class_key}")
    private String headerClassKey;
    private final String channel;
    private final KafkaTemplate<String, E> kafkaTemplate;

    @Override
    public void publish(T source) {
        E event = mapper(source);
        try {
            ProducerRecord<String, E> record = new ProducerRecord<>(channel, event);
            Headers headers = record.headers();
            headers.add(new RecordHeader(headerClassKey,event.getClass().getName().getBytes(StandardCharsets.UTF_8)));
            kafkaTemplate.send(record).get();
//            kafkaTemplate.send(channel, event).get();
            log.info("Published event: {}", event);
            log.debug("Channel: {}", channel);
            log.info("event: {} was published to topic {} with header {} successfully", event, channel, record.headers().lastHeader(KafkaHeaders.RECEIVED_KEY));
        } catch (InterruptedException | ExecutionException e) {
            log.info("event: {} was not published to topic {}", event, channel,e);
        }
    }

    public abstract E mapper(T t);
}

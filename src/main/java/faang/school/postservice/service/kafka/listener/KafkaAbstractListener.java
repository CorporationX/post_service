package faang.school.postservice.service.kafka.listener;

import faang.school.postservice.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@RequiredArgsConstructor
public abstract class KafkaAbstractListener {

    protected final RedisCacheService redisCacheService;

    public abstract void consume(ConsumerRecord<String, Object> message);
}

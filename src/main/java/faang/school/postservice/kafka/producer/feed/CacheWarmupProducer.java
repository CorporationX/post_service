package faang.school.postservice.kafka.producer.feed;

import faang.school.postservice.config.properties.kafka.KafkaTopicsProperties;
import faang.school.postservice.dto.feed.CacheWarmupTask;
import faang.school.postservice.kafka.producer.AbstractKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class CacheWarmupProducer extends AbstractKafkaProducer<CacheWarmupTask> {

    private final KafkaTopicsProperties topics;

    public CacheWarmupProducer(KafkaTemplate<String, Object> kafkaTemplate,
                        KafkaTopicsProperties topics) {
        super(kafkaTemplate);
        this.topics = topics;
    }

    public void publishCacheWarmupTaskEvent(CacheWarmupTask event) {
        log.info("Sending feed cache warmup task message. User IDs: {}", event.subscriberIds());
        publishEvent(topics.feed_warmer(), UUID.randomUUID().toString(), event);
    }
}



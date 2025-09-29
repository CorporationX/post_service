package faang.school.postservice.kafka.producer.warmup;

import faang.school.postservice.config.properties.kafka.FeedKafkaTopicsProperties;
import faang.school.postservice.dto.user.feed.CacheWarmupTask;
import faang.school.postservice.dto.user.feed.HeatUserTask;
import faang.school.postservice.kafka.producer.AbstractKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FeedWarmupProducer extends AbstractKafkaProducer<Object> {

    private final FeedKafkaTopicsProperties topics;

    public FeedWarmupProducer(KafkaTemplate<String, Object> kafkaTemplate,
                              FeedKafkaTopicsProperties topics) {
        super(kafkaTemplate);
        this.topics = topics;
    }

    public void publish(HeatUserTask task) {
        publishEvent(topics.warmer(), String.valueOf(task.userId()), task);
    }

    public void publishBatch(CacheWarmupTask task) {
        publishEvent(topics.warmer(), java.util.UUID.randomUUID().toString(), task);
    }
}
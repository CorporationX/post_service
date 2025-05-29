package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.properties.feed.FeedCacheProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class AbstractKafkaConsumer<T> {

    protected final ObjectMapper objectMapper;
    protected final RedisTemplate<String, Object> redisTemplate;
    protected final FeedCacheProperties feedCacheProperties;

    protected AbstractKafkaConsumer(ObjectMapper objectMapper,
                                    RedisTemplate<String, Object> redisTemplate,
                                    FeedCacheProperties feedCacheProperties) {
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
        this.feedCacheProperties = feedCacheProperties;
    }

    public void consumeRecord(ConsumerRecord<String, String> record, Class<T> clazz) {
        try {
            T event = objectMapper.readValue(record.value(), clazz);
            handleEvent(event);
        } catch (Exception e) {
            logError(e);
        }
    }

    protected abstract void handleEvent(T event);

    protected abstract void logError(Exception e);
}

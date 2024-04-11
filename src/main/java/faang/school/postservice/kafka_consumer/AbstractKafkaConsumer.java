package faang.school.postservice.kafka_consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.service.cache.FeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public abstract class AbstractKafkaConsumer<T> {
    protected FeedService feedService;
    private ObjectMapper objectMapper;

    @Autowired
    public void setFeedService(FeedService feedService) {
        this.feedService = feedService;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public T listen(String message, Class<T> clazz) {
        try {
            return objectMapper.readValue(message, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse message", e);
            throw new RuntimeException("Failed to parse message", e);
        }
    }
}

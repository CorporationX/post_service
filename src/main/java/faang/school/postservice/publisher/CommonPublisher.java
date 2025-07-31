package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    @Async("taskExecutor")
    public void sendRedis(String topic, Object event) {
        redisTemplate.convertAndSend(topic, event);
    }

    @Async("taskExecutor")
    public void sendKafka(String topic, Object event) {
        try {
            String json = mapper.writeValueAsString(event);
            kafkaTemplate.send(topic, String.valueOf(event.hashCode()), json);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
package faang.school.postservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.service.post.RedisCache;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final RedisCache redisCache;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topic.post}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<Long, String> record) {
        Long postId = record.key();
        String usersId = record.value();

        try {
            JsonNode jsonNode = objectMapper.readTree(usersId);
            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    Long userId = node.get("id").asLong();
                    redisCache.addPostToUserFeed(postId, userId);
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

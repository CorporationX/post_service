package faang.school.postservice.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.post.name}")
    private String postTopic;

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void publishKafkaPostEvent(Post post) {
        List<Long> subscriberIds = userServiceClient.getAllFollowers(post.getAuthorId());
        KafkaPostEvent kafkaPostEvent = new KafkaPostEvent(post, subscriberIds);
        try {
            String kafkaPostEventJson = objectMapper.writeValueAsString(kafkaPostEvent);
            kafkaTemplate.send(postTopic, kafkaPostEventJson);
            log.info("Сообщение {} отправлено в Kafka топик {}", kafkaPostEventJson, postTopic);
        } catch (JsonProcessingException e) {
            log.error("Не удалось сериализовать сообщение  в JSON", e);
        }
    }

}

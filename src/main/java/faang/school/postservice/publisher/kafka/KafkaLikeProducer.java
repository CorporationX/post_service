package faang.school.postservice.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.kafka.KafkaLikeEvent;
import faang.school.postservice.dto.like.LikeAndAuthorIdDto;
import faang.school.postservice.dto.user.UserDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.like.name}")
    private String likeTopic;

    @Retryable(retryFor = FeignException.class, maxAttempts = 5, backoff = @Backoff(delay = 1000))
    public void publishKafkaLikeEvent(LikeDto like) {
        UserDto author = userServiceClient.getUser(like.getAuthorId());
        KafkaLikeEvent kafkaLikeEvent = new KafkaLikeEvent(like, author);
        try {
            String kafkaLikeEventJson = objectMapper.writeValueAsString(kafkaLikeEvent);
            kafkaTemplate.send(likeTopic, kafkaLikeEventJson);
            log.info("Сообщение {} отправлено в Kafka топик {}", kafkaLikeEventJson, likeTopic);
        } catch (JsonProcessingException e) {
            log.error("Не удалось сериализовать сообщение  в JSON", e);
        }
    }
    
}

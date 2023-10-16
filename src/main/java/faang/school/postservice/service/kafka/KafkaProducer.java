package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.kafka.KafkaPostDto;
import faang.school.postservice.dto.redis.TimePostId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.feed-topic}")
    private String feedTopicName;
    @Value("${spring.kafka.topics.feed-topic}")
    private String likeTopicName;
    @Value("${spring.kafka.topics.feed-topic}")
    private String commentTopicName;

    @Async("kafkaThreadPool")
    public void sendNewPostInFeed(List<Long> followers, TimePostId timePostId) {
        for (Long follower : followers) {
            var future = kafkaTemplate.send(feedTopicName, KafkaPostDto.builder()
                    .userId(follower)
                    .post(timePostId)
                    .build());
            handleFuture(future);
        }
    }

    private void handleFuture(CompletableFuture<SendResult<String, Object>> future) {
        future.whenComplete(((sendResult, throwable) -> {
            String key = sendResult.getProducerRecord().key();
            Object value = sendResult.getProducerRecord().value();
            if (throwable != null) {
                handleFailure(key, value, throwable);
            } else {
                handleSuccess(key, value, sendResult);
            }
        }));
    }

    private void handleSuccess(String key, Object value, SendResult<String, Object> sendResult) {
        log.info("Message sent successfully for the key: {} and the value: {}, partition is: {}",
                key, value, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(String key, Object value, Throwable throwable) {
        log.error("Error sending message and exception is {}", throwable.getMessage(), throwable);
    }
}

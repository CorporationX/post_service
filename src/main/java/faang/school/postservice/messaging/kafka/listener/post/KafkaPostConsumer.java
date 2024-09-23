package faang.school.postservice.messaging.kafka.listener.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.FollowersPostEvent;
import faang.school.postservice.service.redis.RedisFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final ObjectMapper objectMapper;
    private final RedisFeedService redisFeedService;

    @KafkaListener(topics = "${spring.kafka.topic.posts}",
            groupId = "${spring.kafka.consumer.group-id}",
    containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(String message, Acknowledgment acknowledgment) {
        try {
            FollowersPostEvent followersPostEvent = objectMapper.readValue(message.getBytes(), FollowersPostEvent.class);
            log.info("Слушатель - KafkaPostConsumer, получил обытие {}", followersPostEvent);
            followersPostEvent.getFollowersIds().add(3L);
            redisFeedService.addIdPostsInUserId(followersPostEvent);
            log.info("Событие {} сохранено в БД", followersPostEvent);

            acknowledgment.acknowledge();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
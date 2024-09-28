package faang.school.postservice.consumer;

import faang.school.postservice.dto.publishable.FeedHeaterEvent;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class KafkaFeedHeaterConsumer {
    private final RedisFeedRepository redisFeedRepository;

    @KafkaListener(topics = "${spring.kafka.topic-name.feed-heater}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "containerFactory")
    public void listenFeedHeaterEvent(FeedHeaterEvent feedHeaterEvent, Acknowledgment ack) {
        feedHeaterEvent.getUserIds().forEach(userId -> {
            try {
                redisFeedRepository.getFeed(userId, null);
                ack.acknowledge();
            } catch (Exception e) {
                log.error("error feed creation:", e);
            }
        });
    }
}

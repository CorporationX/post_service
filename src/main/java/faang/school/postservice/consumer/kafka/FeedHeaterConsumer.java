package faang.school.postservice.consumer.kafka;

import faang.school.postservice.cache.redis.FeedCache;
import faang.school.postservice.dto.event.FeedHeaterEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Slf4j
@AllArgsConstructor
public class FeedHeaterConsumer {
    private final FeedCache feedCache;

    @KafkaListener(topics = "${spring.kafka.topics-name.feed-heater}", containerFactory = "containerFactory")
    @Transactional
    public void listenEvent(FeedHeaterEvent event, Acknowledgment ack) {
        log.info("feed heating is starting");
        event.getUserIds().forEach(userId -> {
            log.info("обрабатываем юзера с id = {}", userId);
            try {
                createFeedForOneUser(userId);
                ack.acknowledge();
            } catch (Exception e) {
                log.error("ошибка в процессе создания фида: ", e);
            }
        });
    }


    private void createFeedForOneUser(long userId) {
        feedCache.getFeed(userId, null);
    }
}
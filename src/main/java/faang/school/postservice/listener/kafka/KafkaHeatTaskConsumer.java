package faang.school.postservice.listener.kafka;

import faang.school.postservice.model.event.kafka.HeatTaskEventKafka;
import faang.school.postservice.redis.service.CacheHeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "${spring.kafka.topics.cache-heat}", groupId = "${spring.kafka.consumer.group-id}")
public class KafkaHeatTaskConsumer {

    private final CacheHeatService cacheHeatService;

    @KafkaHandler
    public void handleHeatTask(HeatTaskEventKafka event, Acknowledgment ack) {
        log.info("Received heat task for userId range: {}-{}", event.getStartUserId(), event.getEndUserId());

        cacheHeatService.heatCache(event.getStartUserId(), event.getEndUserId());
        ack.acknowledge();
        log.info("Completed heat task for userId range: {}-{}", event.getStartUserId(), event.getEndUserId());
    }
}

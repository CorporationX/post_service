package faang.school.postservice.kafkalistener;

import faang.school.postservice.dto.kafkaevents.FeedHeatEvent;
import faang.school.postservice.exception.KafkaEventListenException;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class FeedHeatEventListener {

    private final FeedService feedService;

    @KafkaListener(topics = "${spring.data.kafka.topic.heat}",
            containerFactory = "feedHeatEventListenerContainerFactory")
    public void handleEvent(FeedHeatEvent event, Acknowledgment ack) {
        log.info("Начинаю обработку события для юзера {}", event.getId());
        try {
            feedService.heatFeedForUser(event.getId());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка отправки фида юзера {} на прогрев", event.getId());
            throw new KafkaEventListenException("Не удалось обработать событие", e);
        }

    }

}

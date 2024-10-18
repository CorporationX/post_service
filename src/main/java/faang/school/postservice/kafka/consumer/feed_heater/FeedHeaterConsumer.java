package faang.school.postservice.kafka.consumer.feed_heater;

import faang.school.postservice.kafka.consumer.KafkaConsumer;
import faang.school.postservice.kafka.event.feed_heater.FeedHeaterEvent;
import faang.school.postservice.service.feed.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedHeaterConsumer implements KafkaConsumer<FeedHeaterEvent> {

    private final FeedHeaterService feedHeaterService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.feed-heater.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(FeedHeaterEvent event, Acknowledgment ack) {

        log.info("Received new comment like event {}", event);

        feedHeaterService.handleBatch(event.getUsersAndFollowees());

        ack.acknowledge();
    }
}

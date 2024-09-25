package faang.school.postservice.consumer;

import faang.school.postservice.dto.HeaterEvent;
import faang.school.postservice.heater.FeedHeater;
import faang.school.postservice.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaHeaterFeedConsumer extends AbstractConsumer<HeaterEvent> {
    private final FeedHeater feedHeater;

    @Autowired
    public KafkaHeaterFeedConsumer(FeedService feedService, FeedHeater feedHeater) {
        super(feedService);
        this.feedHeater = feedHeater;
    }

    @KafkaListener(topics = "${spring.kafka.topic.heater-feed}",
            groupId = "${spring.kafka.consumer.group-id.heater-group}")
    public void listen(HeaterEvent event, Acknowledgment ack) {
        handle(event, ack, () -> feedHeater.heatFeed());
    }
}

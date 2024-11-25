package faang.school.postservice.service.feedheater;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.consumer.KafkaConsumer;
import faang.school.postservice.mapper.HeatFeedCacheEventMapper;
import faang.school.postservice.model.event.HeatFeedCacheEvent;
import faang.school.postservice.protobuf.generate.HeatFeedCacheEventProto;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedHeaterDistributor implements EventPublisher<HeatFeedCacheEvent>, KafkaConsumer<byte[]> {
    @Value("${spring.kafka.topics.feed-heater.name}")
    private String topic;

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final HeatFeedCacheEventMapper mapper;
    private final FeedService feedService;

    @Override
    @Async(value = "feedThreadPool")
    public void publish(HeatFeedCacheEvent event) {
        kafkaTemplate.send(topic,
                mapper.toProto(event).toByteArray());
        log.info("event {} sent to topic {} ", event, topic);
    }

    @Override
    @KafkaListener(topics = {"${spring.kafka.topics.feed-heater.name}"}, groupId = "first")
    public void processEvent(byte[] message) throws InvalidProtocolBufferException {
        HeatFeedCacheEvent event = mapper.toEvent(HeatFeedCacheEventProto.HeatFeedCacheEvent.parseFrom(message));

        event.getUsersIds().forEach(feedService::generateFeedForUser);
    }
}

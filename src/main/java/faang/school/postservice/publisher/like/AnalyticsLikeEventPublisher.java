package faang.school.postservice.publisher.like;

import faang.school.event.Event;
import faang.school.event.AnalyticsLikeEvent;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class AnalyticsLikeEventPublisher implements EventPublisher {
    private final KafkaTemplate<String, Event> kafkaTemplate;
    private final LikeMapper likeMapper;

    @Value("${spring.kafka.topics.analytics-like-topic.name}")
    private String analyticsLikeTopic;

    @Override
    public void publishEvent(Object dto) {
        AnalyticsLikeEvent event = likeMapper.toAnalyticsLikeEvent((Like) dto);
        event.setAuthorId(((Like) dto).getPost().getAuthorId());
        event.setTimestamp(LocalDateTime.now());
        log.info("Publishing event like postId{} to Kafka:", event.getPostId());
        kafkaTemplate.send(analyticsLikeTopic, event);
    }
}

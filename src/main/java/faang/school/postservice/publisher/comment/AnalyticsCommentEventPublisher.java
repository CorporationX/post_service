package faang.school.postservice.publisher.comment;

import faang.school.event.AnalyticsCommentEvent;
import faang.school.event.Event;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AnalyticsCommentEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, Event> kafkaTemplate;
    private final CommentMapper commentMapper;

    @Value("${spring.kafka.topics.analytics-comment-topic.name}")
    private String analyticsCommentTopicName;

    @Override
    public void publishEvent(Object dto) {
        AnalyticsCommentEvent event = commentMapper.toAnalyticsCommentEvent((Comment) dto);
        kafkaTemplate.send(analyticsCommentTopicName, event);
    }
}

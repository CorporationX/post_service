package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.comment.CommentPublishedEvent;
import faang.school.postservice.mapper.comment.CommentPublishedEventMapper;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentProducer implements EventPublisher<CommentPublishedEvent> {
    @Value("${spring.kafka.topics.comments.name}")
    private String topic;

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final CommentPublishedEventMapper commentPublishedEventMapper;

    @Override
    public void publish(CommentPublishedEvent event) {
        kafkaTemplate.send(topic, commentPublishedEventMapper.toProto(event).toByteArray());
        log.info("event {} sent to topic {} ", event, topic);
    }
}

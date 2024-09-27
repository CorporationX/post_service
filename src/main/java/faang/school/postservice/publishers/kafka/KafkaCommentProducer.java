package faang.school.postservice.publishers.kafka;

import faang.school.postservice.events.CommentEventForKafka;
import faang.school.postservice.events.Event;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentProducer {
    private final KafkaTemplate<String, Event> eventKafkaTemplate;
    private final CommentMapper commentMapper;

    @Value(value = "${spring.data.kafka.topic.comments_topic}")
    private String commentsTopic;

    public void sendMessage(Comment comment) {
        CommentEventForKafka event = commentMapper.toCommentEventForKafka(comment);
        CompletableFuture<SendResult<String, Event>> future = eventKafkaTemplate.send(commentsTopic, event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent comment event = [{}] with offset = [{}]", event, result.getRecordMetadata().offset());
            } else {
                log.info("Unable to send comment event = [{}] due to : {}", event, ex.getMessage());
            }
        });
    }
}

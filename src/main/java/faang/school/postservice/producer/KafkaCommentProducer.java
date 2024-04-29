package faang.school.postservice.producer;

import faang.school.postservice.dto.event.CommentEventKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentProducer
        extends AbstractKafkaProducer<CommentEventKafka> {

    @Value(value = "${spring.kafka.topics.comment}")
    private String topicComment;

    @Async("executor")
    public void sendMessage(CommentEventKafka commentEventKafka) {
        sendMessage(commentEventKafka, topicComment);
    }
}

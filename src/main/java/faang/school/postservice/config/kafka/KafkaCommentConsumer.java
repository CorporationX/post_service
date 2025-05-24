package faang.school.postservice.config.kafka;

import faang.school.postservice.dto.comment.CommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaCommentConsumer {
    @KafkaListener(topics = "${spring.kafka.listener.topics.news-feed-comment}",
            groupId = "${spring.kafka.listener.groupId.news-feed-group}")
    public void listen(CommentEvent event) {
        log.info("Received message {}", event);
    }
}

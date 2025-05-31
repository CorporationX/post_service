package faang.school.postservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.AbstractKafkaProducer;
import faang.school.postservice.dto.comment.CommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaCommentProducer extends AbstractKafkaProducer {
    @Value("${spring.kafka.producer.topics.news-feed-comment}")
    private String newsFeedCommentTopic;

    public KafkaCommentProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    public void send(CommentEvent comment) {
        send(newsFeedCommentTopic, comment);
        log.info("News feed comment sent to topic: " + newsFeedCommentTopic);
    }
}

package faang.school.postservice.producer;

import faang.school.postservice.dto.avro.CommentCreatedEventAvro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * KafkaCommentProducer — продюсер для ивента создания комментария
 *
 * @author bozya
 * @since 26.09.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.comments}")
    private String commentTopics;

    public void sendComment(CommentCreatedEventAvro comment) {
        kafkaTemplate.send(commentTopics, comment);
    }
}
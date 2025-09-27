package faang.school.postservice.producer;

import faang.school.postservice.dto.avro.CommentCreatedEventAvro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

/**
 * KafkaCommentProducer — продюсер для ивента создания комментария
 *
 * @author bozya
 * @since 26.09.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaCommentProducer {

    private final RetryTemplate retryTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.comments}")
    private String commentTopics;

    public void sendCommentWithRetry(CommentCreatedEventAvro comment) {
        try {
            retryTemplate.execute(context -> {
                kafkaTemplate.send(commentTopics, comment);
                return null;
            });
        } catch (Exception e) {
            log.error("Комментарий не получилось отправить после нескольких попыток");
        }

    }
}
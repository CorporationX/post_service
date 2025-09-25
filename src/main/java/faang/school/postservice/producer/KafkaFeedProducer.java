package faang.school.postservice.producer;

import faang.school.postservice.dto.avro.FeedBatchEventAvro;
import faang.school.postservice.publisher.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Продюсер для отправки ивентов в топик {@code feeds}
 *
 * @author Linempy
 * @since 23.09.2025
 */
@Component
@RequiredArgsConstructor
public class KafkaFeedProducer implements EventProducer<FeedBatchEventAvro> {

    @Value("${kafka.topics.feeds}")
    private String topicForFeed;

    private final KafkaTemplate<String, FeedBatchEventAvro> kafkaTemplate;

    @Override
    public void sendMessage(FeedBatchEventAvro event) {
        kafkaTemplate.send(topicForFeed, event);
    }
}
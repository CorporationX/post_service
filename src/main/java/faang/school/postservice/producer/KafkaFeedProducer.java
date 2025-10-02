package faang.school.postservice.producer;

import faang.school.postservice.dto.avro.FeedBatchEventAvro;
import faang.school.postservice.publisher.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Продюсер для отправки ивентов в топик {@code feeds}
 *
 * @author Linempy
 * @since 23.09.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaFeedProducer implements EventProducer<FeedBatchEventAvro> {

    @Value("${spring.kafka.topics.feeds}")
    private String topicForFeed;

    private final KafkaTemplate<String, FeedBatchEventAvro> kafkaTemplate;

    @Override
    public void sendMessage(FeedBatchEventAvro event) {
        CompletableFuture<SendResult<String, FeedBatchEventAvro>> future = kafkaTemplate.send(topicForFeed, event);

        future.whenComplete((success, failure) -> {
            if (failure == null) {
                log.info("Ивент был успешно отправлен в топик: {}", topicForFeed);
            } else {
                log.warn("Ивент не был отправлен в топик: {}", topicForFeed);
            }
        });
    }
}
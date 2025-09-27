package faang.school.postservice.consumer;

import faang.school.postservice.dto.avro.FeedBatchEventAvro;
import faang.school.postservice.repository.redis.FeedRedisRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Слушатель ивентов {@link FeedBatchEventAvro}
 *
 * @author Linempy
 * @since 24.09.2025
 */
@Service
@RequiredArgsConstructor
public class KafkaFeedConsumer {

    private final FeedRedisRepository feedRedisRepository;

    @KafkaListener(topics = "${spring.kafka.topics.feeds}")
    public void read(ConsumerRecord<String, FeedBatchEventAvro> consumerRecord) {
        FeedBatchEventAvro event = consumerRecord.value();
        feedRedisRepository.updateFeedViaBatch(event);
    }
}
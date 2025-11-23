package faang.school.postservice.consumer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.avro.FeedBatchEventAvro;
import faang.school.postservice.dto.avro.PostPublishedEventAvro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Recover;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Слушатель топика posts.
 * Принимает ивент {@link PostPublishedEventAvro} и по нему получает подписчиков пользователя и отправляет
 * ивент {@link FeedBatchEventAvro} в топик feeds. Отправка реализована по батчам подписок пользователя
 *
 * @author Linempy
 * @since 22.09.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPostConsumer {

    @Value("${kafka.events.feed.posts.batch-size:10000}")
    private int batchSize;

    @Value("${spring.kafka.topics.feeds}")
    private String feedBatchTopic;

    private final KafkaTemplate<String, FeedBatchEventAvro> kafkaTemplate;
    private final UserServiceClient userClient;
    private final UserContext userContext;

    @KafkaListener(topics = "${spring.kafka.topics.posts}")
    public void processSendEvent(ConsumerRecord<String, PostPublishedEventAvro> consumerRecord) {
        PostPublishedEventAvro event = consumerRecord.value();
        CompletableFuture.runAsync(() -> processBatchesAsync(event));
    }

    @Async("feedExecutor")
    public void processBatchesAsync(PostPublishedEventAvro event) {
        Long authorId = Long.valueOf(event.getAuthorId());

        List<Long> subscriberIds = getSubscribersWithRetry(authorId);

        if (subscriberIds.isEmpty()) {
            log.info("Нет подписчиков для автора: {}", authorId);
            return;
        }

        processAndSendBatches(event, subscriberIds);
    }


    public List<Long> getSubscribersWithRetry(Long authorId) {
        log.info("Попытка получить подписчиков для автора: {}", authorId);
        userContext.setUserId(authorId);
        ResponseEntity<List<Long>> response = userClient.getFollowerIds(authorId);

        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) {
            throw new RuntimeException("HTTP " + response.getStatusCode() + " для автора: " + authorId);
        }

        log.info("Получено {} подписчиков для автора: {}", Objects.requireNonNull(response.getBody()).size(), authorId);
        return response.getBody();
    }

    @Recover
    public List<Long> getSubscribersFallback(Exception ex, Long authorId) {
        log.error("Все попытки получения подписчиков провалились для автора: {}", authorId, ex);
        return Collections.emptyList();
    }

    private void processAndSendBatches(PostPublishedEventAvro event, List<Long> subscriberIds) {
        int totalBatches = (int) Math.ceil((double) subscriberIds.size() / batchSize);

        for (int i = 0; i < subscriberIds.size(); i += batchSize) {
            int end = Math.min(i + batchSize, subscriberIds.size());
            List<Long> batch = subscriberIds.subList(i, end);
            int batchNumber = i / batchSize + 1;

            sendBatchToKafka(event, batch, batchNumber, totalBatches);
        }
    }

    private void sendBatchToKafka(PostPublishedEventAvro event, List<Long> batch,
                                  int batchNumber, int totalBatches) {
        List<String> subscriberIdsString = batch.stream()
                .map(String::valueOf)
                .toList();

        FeedBatchEventAvro batchEvent = new FeedBatchEventAvro(
                event.getPostId(),
                event.getAuthorId(),
                subscriberIdsString,
                batchNumber,
                totalBatches,
                event.getPublishedAt()
        );

        kafkaTemplate.send(feedBatchTopic, batchEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        handleSuccessBatching(event.getPostId(), batchNumber, totalBatches);
                    } else {
                        handleFailedBatching(event.getPostId(), batchNumber, totalBatches, ex);
                    }
                });
    }

    private void handleSuccessBatching(String postId, int batchNumber, int totalBatches) {
        log.info("Было отправлено {}/{} батчей для поста {}", batchNumber, totalBatches, postId);
    }

    private void handleFailedBatching(String postId, int batchNumber, int totalBatches, Throwable ex) {
        log.error("Ошибка в отправки батчей {}/{} для поста {}", batchNumber, totalBatches, postId, ex);
    }

}
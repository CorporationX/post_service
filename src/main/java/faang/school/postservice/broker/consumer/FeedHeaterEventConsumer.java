package faang.school.postservice.broker.consumer;

import faang.school.postservice.dto.feed.FeedHeaterEvent;
import faang.school.postservice.service.feed.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedHeaterEventConsumer {

    private final FeedHeaterService feedHeaterService;
    private final AsyncTaskExecutor asyncTaskExecutor;

    @KafkaListener(
            topics = "${spring.kafka.topic.feed-heater-topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "feedHeaterEventContainerFactory")
    public void consume(FeedHeaterEvent feedHeaterEvent, Acknowledgment acknowledgment) {
        List<Long> userIds = feedHeaterEvent.userIds();
        CompletableFuture.runAsync(() ->
                                feedHeaterService.heatFeedByUsersList(userIds),
                        asyncTaskExecutor)
                .thenAccept(res -> {
                    log.info("Feed heat event with user ids {} processed", userIds);
                    acknowledgment.acknowledge();
                })
                .exceptionally(exception -> {
                    log.error("Error consuming feed heat event with user ids {}. Error: {}",
                            userIds, exception.getMessage());
                    return null;
                });
    }
}

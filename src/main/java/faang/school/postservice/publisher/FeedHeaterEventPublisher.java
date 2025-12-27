package faang.school.postservice.publisher;

import faang.school.postservice.event.FeedHeaterEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedHeaterEventPublisher {

    private final KafkaTemplate<String, FeedHeaterEvent> kafkaTemplate;

    @Value("${kafka.topic.feed-heater}")
    private String feedHeaterTopic;

    public void publish(FeedHeaterEvent feedHeaterEvent) {
        CompletableFuture<SendResult<String, FeedHeaterEvent>> future = kafkaTemplate.send(feedHeaterTopic, feedHeaterEvent);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send feed heater event");
            } else {
                log.info("Sent feed heater event");
            }
        });
    }
}
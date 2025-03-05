package faang.school.postservice.kafka.producer;

import com.google.common.collect.Lists;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.kafka.event.FeedUpdateEvent;
import faang.school.postservice.kafka.event.NewPostEvent;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;

    @Value("${spring.data.cache.warmup.batch-size}")
    private int batchSize;;

    @SneakyThrows
    public void sendPostCreatedEvent(@NotNull NewPostEvent event) {
        kafkaTemplate.send("new-posts", event);
        List<Long> subscribers = getSubscribers(event);

        Lists.partition(subscribers, batchSize)
                .forEach(batch -> sendFeedUpdateEvent(event, batch));

        log.info("Sent feed update events for post {} with {} subscribers in {} batches",
                event.getPostId(), subscribers.size(),
                (subscribers.size() + batchSize - 1) / batchSize);
    }

    private void sendFeedUpdateEvent(NewPostEvent postEvent, List<Long> subscribers) {
        try {
            FeedUpdateEvent feedEvent = FeedUpdateEvent.builder()
                    .postId(postEvent.getPostId())
                    .authorId(postEvent.getAuthorId())
                    .subscriberIds(subscribers)
                    .build();

            kafkaTemplate.send("feed-updates", feedEvent)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to send feed update event for post {} batch",
                                    postEvent.getPostId(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error sending feed update event for post {}",
                    postEvent.getPostId(), e);
            throw e;
        }
    }

    private List<Long> getSubscribers(NewPostEvent event) {
        try {
            if (event.getAuthorId() != null) {
                return userServiceClient.getUserSubscribersIds(event.getAuthorId());
            } else if (event.getProjectId() != null) {
                return userServiceClient.getProjectSubscriptions(event.getProjectId());
            } else {
                log.error("Post {} has neither author nor project", event.getPostId());
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("Failed to get subscribers for post {}", event.getPostId(), e);
            throw e;
        }
    }
}
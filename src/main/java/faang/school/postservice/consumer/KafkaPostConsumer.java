package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.PostEvent;
import faang.school.postservice.exception.InvalidPostEventException;
import faang.school.postservice.exception.SubscriberProcessingException;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {
    private final ObjectMapper objectMapper;
    private final FeedService feedService;

    @KafkaListener(topics = "${spring.data.kafka.topic.post.name}",
            groupId = "${spring.data.kafka.topic.post.group}")
    public void listen(String postEventString, Acknowledgment ack) {
        try {
            PostEvent postEvent = objectMapper.readValue(postEventString, PostEvent.class);
            validatePostEvent(postEvent);
            List<Long> failedSubscribers = postEvent.subscriberIds().stream()
                    .filter(subscriberId -> !processSubscriber(subscriberId, postEvent))
                    .toList();

            checkFailedSubscribers(failedSubscribers);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Произошла ошибка при десериализации ивента PostEvent", e);
        }
    }

    private void checkFailedSubscribers(List<Long> failedSubscribers) {
        if (!failedSubscribers.isEmpty()) {
            throw new SubscriberProcessingException("Не удалось обработать подписчиков: " + failedSubscribers);
        }
    }

    private void validatePostEvent(PostEvent postEvent) {
        if (postEvent == null || postEvent.subscriberIds() == null) {
            throw new InvalidPostEventException("Ивент или ID подписчика не может быть null");
        }
    }

    private boolean processSubscriber(long subscriberId, PostEvent postEvent) {
        try {
            feedService.addPostToFeed(subscriberId, postEvent);
            return true;
        } catch (Exception e) {
            log.error("Ошибка при обработке ID подписчика: {}", subscriberId, e);
            return false;
        }
    }
}
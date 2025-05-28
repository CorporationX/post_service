package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostPublishDto;
import faang.school.postservice.dto.user.UserViewDto;
import faang.school.postservice.exception.KafkaPostPublisherException;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostEventPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Сервисный класс `PostToKafkaSender` для отправки запросов в Kafka/
 * Добавляет в топик данные о посте.
 *
 * <p>Основные методы:
 * <ul>
 *     <li>{@link #putPostToKafka(Post)} - Отправка данных о Post в Kafka.</li>
 * </ul>
 * </p>
 *
 * @author marsel_mkh
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class PostKafkaEventPublisher {
    private static final int KAFKA_BACKOFF_DELAY = 2000;
    private static final int KAFKA_BACKOFF_MULTIPLIER = 2;
    private final UserServiceClient userServiceClient;
    private final PostEventPublisher postEventPublisher;

    /**
     * Формирует объект для отправки в Kafka.
     *
     * @param post сущность сохраненная в БД
     */
    @Async("postToKafkaExecutor")
    public void putPostToKafka(Post post) {
        Long authorId = post.getAuthorId();
        List<Long> subscriberIds = getSubscriberIds(authorId);

        if (subscriberIds.isEmpty()) {
            log.warn("AuthorId {} has no followers", authorId);
            return;
        }

        sendEvent(post, subscriberIds);
    }

    @Retryable(retryFor = KafkaPostPublisherException.class,
            backoff = @Backoff(
                    delay = KAFKA_BACKOFF_DELAY,
                    multiplier = KAFKA_BACKOFF_MULTIPLIER))
    private void sendEvent(Post post, List<Long> subscribers) {
        PostPublishDto event = PostPublishDto.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .subscribersIds(subscribers)
                .build();

        try {
            postEventPublisher.publish(event);
            log.debug("Event: {} published", event);
        } catch (Exception e) {
            log.error("Failed publish event: {} {}", event, e.getMessage());
            throw new KafkaPostPublisherException("Failed publish event: " + event, e);
        }
    }

    @Retryable(retryFor = FeignException.class,
            backoff = @Backoff(
                    delay = KAFKA_BACKOFF_DELAY,
                    multiplier = KAFKA_BACKOFF_MULTIPLIER))
    private List<Long> getSubscriberIds(long authorId) {
        return userServiceClient.getFollowerIds(authorId).stream()
                .map(UserViewDto::getId)
                .toList();
    }
}

package faang.school.postservice.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.LastCommentDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.events.PostEvent;
import faang.school.postservice.events.PostViewEvent;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.service.redis.FeedCacheService;
import faang.school.postservice.service.redis.PostCacheService;
import faang.school.postservice.service.redis.UserCacheService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {
    private final UserCacheService userCacheService;
    @Value("${kafka.header_class_key}")
    private String headerKey;

    private final ObjectMapper objectMapper;
    private final FeedCacheService feedCacheService;
    private final PostCacheService postCacheService;
    private Map<Class<?>, Consumer<Object>> eventActions = new HashMap<>();

    @PostConstruct
    public void init() {
        log.info("Initializing Kafka Consumer");
        eventActions.put(PostEvent.class, this::handlePostEvent);
        eventActions.put(PostViewEvent.class, this::handlePostViewEvent);
        eventActions.put(LikeEvent.class, this::handleLikeEvent);
        eventActions.put(CommentEvent.class, this::handleCommentEvent);
        log.info("Finished initialization of Kafka Consumer");
    }

    @KafkaListener(topics = {"#{'${kafka.topics.like_event}'.split(',')}",
            "#{'${kafka.topics.comment_event}'.split(',')}",
            "#{'${kafka.topics.post_event}'.split(',')}",
            "#{'${kafka.topics.post_view_event}'.split(',')}"}, groupId = "kafka_consumer_group")
    public void consume(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment) {
        String topic = record.topic();
        log.info("received message: {} from topic {}", record.value(), topic);

        try {
            String header = getHeader(record, headerKey);
            Object message = record.value();
            Class<?> c = Class.forName(header);
            Object deserializedMessage = convertToClass(message, c);
            log.info("got message {}", deserializedMessage);
            handleEvent(c, deserializedMessage, acknowledgment);
        } catch (Exception e) {
            log.error("Error processing message from topic {}: {}", topic, e.getMessage(), e);
            throw new RuntimeException("Failed to process Kafka message", e);
        }
    }

    private <T> T convertToClass(Object message, Class<T> clazz) {
        log.info("converting message {} to class {}", message, clazz.getName());
        return objectMapper.convertValue(message, clazz);
    }

    private String getHeader(ConsumerRecord<String, Object> record, String headerKey) {
        if (record.headers().lastHeader(headerKey).value() == null) {
            log.error("did not managed to find header to deserialize message");
            throw new IllegalArgumentException("No header found for key " + headerKey);
        }
        log.info("got header value {}", record.headers().lastHeader(headerKey));
        return new String(record.headers().lastHeader(headerKey).value());
    }

    private void handleEvent(Class<?> clazz, Object message, Acknowledgment acknowledgment) {
        Consumer<Object> action = eventActions.get(clazz);
        if (action != null) {

            try {
                action.accept(message);
                log.info("action accepted {}", message);
                acknowledgment.acknowledge();

                log.info("processed message {} finished:", message);
            } catch (Exception e) {
                log.error("Error processing event {}", message, e);
            }
        } else {
            log.error("did not managed to find action for class {}", clazz.getName());
            throw new IllegalArgumentException("No action found for key " + clazz.getName());
        }
    }

    private void handlePostEvent(Object message) {
        log.info("handling post event");
        PostEvent event = objectMapper.convertValue(message, PostEvent.class);
        event.getPostFollowersIds().forEach(userId -> feedCacheService.addPost(userId, event.getPostId()));
    }

    private void handlePostViewEvent(Object message) {
        log.info("updating post view");
        PostViewEvent event = objectMapper.convertValue(message, PostViewEvent.class);
        postCacheService.incrementPostView(event.getPostId());
    }

    private void handleLikeEvent(Object message) {
        log.info("updating like event");
        LikeEvent event = objectMapper.convertValue(message, LikeEvent.class);
        postCacheService.incrementLike(event.getPostId());
    }

    private void handleCommentEvent(Object message) {
        log.info("updating comment event");
        CommentEvent event = objectMapper.convertValue(message, CommentEvent.class);
        RedisUser redisUser = userCacheService.findUserById(event.getAuthorId());
        LastCommentDto lastCommentDto = new LastCommentDto(event.getAuthorId(), event.getCommentContent(), redisUser.getUserInfo().getUsername(), event.getSendAt());
        postCacheService.addCommentToPost(event.getPostId(), lastCommentDto);
    }
}

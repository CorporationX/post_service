package faang.school.postservice.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.events.Event;
import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.events.PostEvent;
import faang.school.postservice.events.PostViewEvent;
import faang.school.postservice.service.redis.FeedCacheService;
import faang.school.postservice.service.redis.PostCacheService;
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
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
//@KafkaListener(topics = {"${kafka.topics.like_event},${kafka.topics.comment_event},${kafka.topics.post_event},${kafka.topics.post_view_event}"})
@KafkaListener(topics = {"#{'${kafka.topics.like_event}'.split(',')}",
        "#{'${kafka.topics.comment_event}'.split(',')}",
        "#{'${kafka.topics.post_event}'.split(',')}",
        "#{'${kafka.topics.post_view_event}'.split(',')}"})
public class KafkaConsumer {
    @Value("${kafka.header_class_key}")
    private String headerKey;

    @Value("${kafka.topics.post_event}")
    private String postTopic;
    @Value("${kafka.topics.post_view_event}")
    private String postViewTopic;
    @Value("${kafka.topics.like_event}")
    private String likeTopic;
    @Value("${kafka.topics.comment_event}")
    private String commentTopic;

    private final ObjectMapper objectMapper;
    private final FeedCacheService feedCacheService;
    private final PostCacheService postCacheService;
    private Acknowledgment acknowledgment;
//    private Map<String, Consumer<Object>> actions = new HashMap<>();
    private Map<Class<?>, Consumer<Object>> eventActions = new HashMap<>();

    @PostConstruct
    public void init() {
        eventActions.put(PostEvent.class, this::handlePostEvent);
        eventActions.put(PostViewEvent.class, this::handlePostViewEvent);
        eventActions.put(LikeEvent.class, this::handleLikeEvent);
        eventActions.put(CommentEvent.class, this::handleCommentEvent);
    }

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
                log.info("processed message {}", message);
                acknowledgment.acknowledge();
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
        event.getPostFollowersIds().forEach(userId->feedCacheService.addPost(userId,event.getPostId()));
    }
    private void handlePostViewEvent(Object message) {
        log.info("updating post view");
    }
    private void handleLikeEvent(Object message) {
        log.info("updating like event");
    }
    private void handleCommentEvent(Object message) {
        log.info("updating comment event");
    }
}

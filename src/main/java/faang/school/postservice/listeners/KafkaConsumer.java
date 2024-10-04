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
    private Map<String, Consumer<Object>> actions = new HashMap<>();
    private Map<Class<? extends Event>, Consumer<Event>> eventActions = new HashMap<>();

    @PostConstruct
    public void init() {
        actions.put(postTopic, this::updatePost);
        actions.put(postViewTopic, this::updateFeed);
        actions.put(likeTopic, this::updatePost);
        actions.put(commentTopic, this::updatePost);
        eventActions.put(PostEvent.class, this::handlePostEvent);
        eventActions.put(PostViewEvent.class, this::handlePostViewEvent);
        eventActions.put(LikeEvent.class, this::handleLikeEvent);
        eventActions.put(CommentEvent.class, this::handleCommentEvent);
    }

    public void consume(ConsumerRecord<String, Object> record) {
        String topic = record.topic();
        log.info("received message: {} from topic {}", record.value(), topic);

        try {
            String header = getHeader(record, headerKey);
            Object message = record.value();
            Class<?> c = Class.forName(header);
            Object deserializedMessage = convertToClass(message, c);
            log.info("got message {}", deserializedMessage);
            handleEvent(topic, deserializedMessage);
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

    private void handleEvent(String key, Object message) {
        Consumer<Object> action = actions.get(key);
        if (action != null) {
            action.accept(message);
        } else {
            log.error("did not managed to find action for key {}", key);
            throw new IllegalArgumentException("No action found for key " + key);
        }
    }

    private void updateFeed(Object message) {
        log.info("updating feed");
        feedCacheService.updateFeed(message);
    }

    private void updatePost(Object message) {
        log.info("updating post");
        postCacheService.updatePost(message);
    }

    private void handlePostEvent(Object message) {
        log.info("handling post event");

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

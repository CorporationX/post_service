package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.event.KafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaProducer<T extends KafkaEvent> implements KafkaProducer<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, NewTopic> topicMap;

    @Override
    @Async("kafkaThreadPool")
    public void produce(T event) {

        NewTopic newCommentTopic = topicMap.get(getTopic());
        kafkaTemplate.send(newCommentTopic.name(), event);
        log.info("Published new event to Kafka - {}: {}", newCommentTopic.name(), event);
    }

    @Override
    @Async("kafkaThreadPool")
    public void produce(T event, Runnable runnable) {

        NewTopic newCommentTopic = topicMap.get(getTopic());
        kafkaTemplate.send(newCommentTopic.name(), event).thenRun(runnable);
        log.info("Published new event to Kafka with a runnable task - {}: {}", newCommentTopic.name(), event);
    }

    protected abstract String getTopic();
}

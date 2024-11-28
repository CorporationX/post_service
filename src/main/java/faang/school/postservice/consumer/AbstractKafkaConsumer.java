package faang.school.postservice.consumer;

import faang.school.postservice.service.kafka.KafkaReceivedEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaConsumer<E, S extends KafkaReceivedEventService<E>> {
    private final S service;
    private final NewTopic topic;

    public void process(E event) {
        log.info("Received event: {} from topic: {}", event, topic.name());
        service.receiveFromBroker(event);
    }

    public abstract void listen(E event);
}

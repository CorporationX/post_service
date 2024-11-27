package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.mapper.post.PostPublishedEventMapper;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostProducer implements EventPublisher<PostPublishedEvent> {

    @Value("${spring.kafka.topics.posts.name}")
    private String topic;

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final PostPublishedEventMapper postPublishedEventMapper;

    @Override
    public void publish(PostPublishedEvent event) {
        kafkaTemplate.send(topic, postPublishedEventMapper.toProto(event).toByteArray());
        log.info("event {} sent to topic {} ", event, topic);
    }
}

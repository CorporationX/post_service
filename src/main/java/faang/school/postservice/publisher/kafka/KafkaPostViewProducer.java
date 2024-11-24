package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.mapper.PostViewEventMapper;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewProducer implements EventPublisher<PostViewEvent> {
    @Value("${spring.kafka.topics.post_views.name}")
    private String topic;

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final PostViewEventMapper postViewEventMapper;

    @Override
    public void publish(PostViewEvent event) {
        kafkaTemplate.send(topic, postViewEventMapper.toProto(event).toByteArray());
        log.info("event {} sent to topic {} ", event, topic);
    }
}

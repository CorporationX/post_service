package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.mapper.LikePostEventMapper;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeProducer implements EventPublisher<LikePostEvent> {
    @Value("${spring.kafka.topics.likes.name}")
    private String topic;

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final LikePostEventMapper likePostEventMapper;

    @Override
    public void publish(LikePostEvent event) {
        kafkaTemplate.send(topic, likePostEventMapper.toProto(event).toByteArray());
        log.info("event {} sent to topic {} ", event, topic);
    }
}

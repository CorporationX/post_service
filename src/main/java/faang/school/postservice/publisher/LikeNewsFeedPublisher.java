package faang.school.postservice.publisher;

import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.event.LikeEvent;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeNewsFeedPublisher implements EventPublisher<LikeEvent> {

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final LikeMapper likeMapper;

    @Value("${spring.kafka.topics.like-for-feed.name}")
    private String topicName;

    @Override
    public void publish(LikeEvent event) {
        FeedEventProto.FeedEvent protoEvent = likeMapper.toProto(event);
        byte[] byteEvent = protoEvent.toByteArray();
        kafkaTemplate.send(topicName, byteEvent);
    }
}

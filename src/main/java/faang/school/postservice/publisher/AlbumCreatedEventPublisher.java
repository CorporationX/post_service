package faang.school.postservice.publisher;

import faang.school.postservice.dto.album.AlbumCreatedEvent;
import faang.school.postservice.mapper.album.AlbumCreatedEventMapper;
import faang.school.postservice.protobuf.generate.AlbumCreatedEventProto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumCreatedEventPublisher implements EventPublisher<AlbumCreatedEvent> {

    private final KafkaTemplate<byte[], byte[]> kafkaTemplate;
    private final AlbumCreatedEventMapper albumCreatedEventMapper;

    @Value("${spring.kafka.topics.album-created.name}")
    private String topicName;

    @Override
    public void publish(AlbumCreatedEvent event) {
        AlbumCreatedEventProto.AlbumCreatedEvent eventProto = albumCreatedEventMapper.toFollowerEvent(event);
        byte[] byteEvent = eventProto.toByteArray();
        kafkaTemplate.send(topicName, byteEvent);
    }
}

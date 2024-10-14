package faang.school.postservice.publisher;

import faang.school.postservice.dto.album.AlbumCreatedEvent;
import faang.school.postservice.mapper.album.AlbumCreatedEventMapper;
import faang.school.postservice.protobuf.generate.AlbumCreatedEventProto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumCreatedEventPublisherTest {

    @Mock
    private KafkaTemplate<byte[], byte[]> kafkaTemplate;

    @Mock
    private AlbumCreatedEventMapper albumCreatedEventMapper;

    @InjectMocks
    private AlbumCreatedEventPublisher albumCreatedEventPublisher;

    private AlbumCreatedEvent albumCreatedEvent;
    private AlbumCreatedEventProto.AlbumCreatedEvent albumCreatedEventProto;
    private String topicName;

    @BeforeEach
    void setUp() {
        topicName = "topicName";
        ReflectionTestUtils.setField(albumCreatedEventPublisher, "topicName", topicName);
        albumCreatedEvent = new AlbumCreatedEvent();
        albumCreatedEventProto = AlbumCreatedEventProto.AlbumCreatedEvent.newBuilder().build();
    }

    @Test
    void publish_shouldConvertEventAndSendToKafka() {
        when(albumCreatedEventMapper.toFollowerEvent(albumCreatedEvent)).thenReturn(albumCreatedEventProto);
        byte[] byteEvent = albumCreatedEventProto.toByteArray();

        albumCreatedEventPublisher.publish(albumCreatedEvent);

        verify(albumCreatedEventMapper).toFollowerEvent(albumCreatedEvent);
        verify(kafkaTemplate).send(topicName, byteEvent);
    }
}

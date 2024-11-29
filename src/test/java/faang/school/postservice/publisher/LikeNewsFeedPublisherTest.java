package faang.school.postservice.publisher;

import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.event.LikeEvent;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeNewsFeedPublisherTest {

    @Mock
    private KafkaTemplate<byte[], byte[]> kafkaTemplate;

    @Spy
    private LikeMapperImpl likeMapper;

    @InjectMocks
    private LikeNewsFeedPublisher likeNewsFeedPublisher;

    private String topicName;

    @BeforeEach
    void setUp() {
        topicName = "like-feed-topic";
        ReflectionTestUtils.setField(likeNewsFeedPublisher, "topicName", topicName);
    }

    @Test
    void publish_shouldSendLikeEventToKafka() {
        long postId = 123L;
        LikeEvent likeEvent = LikeEvent.builder()
                .postId(postId)
                .build();
        FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.newBuilder()
                .setPostId(postId)
                .build();
        byte[] byteEvent = feedEvent.toByteArray();

        likeNewsFeedPublisher.publish(likeEvent);

        verify(likeMapper).toProto(likeEvent);
        verify(kafkaTemplate).send(topicName, byteEvent);
    }
}

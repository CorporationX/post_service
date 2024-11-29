package faang.school.postservice.publisher;

import faang.school.postservice.protobuf.generate.FeedEventProto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ViewPostNewsFeedPublisherTest {

    @Mock
    private KafkaTemplate<byte[], byte[]> kafkaTemplate;

    @InjectMocks
    private PostNewsFeedPublisher postNewsFeedPublisher;

    private String topicName;

    @BeforeEach
    void setUp() {
         topicName = "post_news_feed";
        ReflectionTestUtils.setField(postNewsFeedPublisher, "topicName", topicName);
    }

    @Test
    void publish_shouldSendPostEventToKafka() {
        FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.newBuilder()
                .setPostId(123L)
                .build();
        byte[] byteEvent = feedEvent.toByteArray();

        postNewsFeedPublisher.publish(feedEvent);

        verify(kafkaTemplate).send(topicName, byteEvent);
    }
}

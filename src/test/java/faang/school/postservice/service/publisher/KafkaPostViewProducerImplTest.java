package faang.school.postservice.service.publisher;

import faang.school.postservice.event.PostViewEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaPostViewProducerImplTest {

    @Mock
    private KafkaTemplate<String, PostViewEvent> kafkaTemplate;

    @InjectMocks
    private KafkaPostViewProducerImpl kafkaPostViewProducer;

    private static final String TEST_TOPIC = "post-viewed-events";

    private Long postId;
    private Long userId;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(kafkaPostViewProducer, "topic", TEST_TOPIC);
        Long postId = 1L;
        Long userId = 100L;
    }

    @Test
    public void publishPostViewEventShouldSendEventToKafkaSuccessfully() {
        kafkaPostViewProducer.publishPostViewEvent(postId, userId);

        verify(kafkaTemplate).send(eq(TEST_TOPIC), any(PostViewEvent.class));
    }

    @Test
    public void publishPostViewEventShouldCreateCorrectEvent() {
        kafkaPostViewProducer.publishPostViewEvent(postId, userId);

        verify(kafkaTemplate).send(eq(TEST_TOPIC), argThat(event -> {
            assertThat(event.getPostId()).isEqualTo(postId);
            assertThat(event.getUserId()).isEqualTo(userId);
            assertThat(event.getViewedAt()).isNotNull();
            return true;
        }));
    }
}

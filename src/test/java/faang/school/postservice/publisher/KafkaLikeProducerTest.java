package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikePostEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
public class KafkaLikeProducerTest {
    @Mock
    private KafkaTemplate<String, LikePostEvent> kafkaTemplate;

    @InjectMocks
    private KafkaLikeProducer kafkaLikeProducer;

    private static final String TOPIC_NAME = "like";
    private final LikePostEvent likePostEvent = new LikePostEvent();

    @BeforeEach
    void setUp() {
        setField(kafkaLikeProducer, "likeTopicName", TOPIC_NAME);
    }

    @Test
    @DisplayName("Публикация события лайка - успешный сценарий")
    public void givenValidLikeEvent_WhenPublish_ThenEventSentToKafka() {
        kafkaLikeProducer.sendMessage(likePostEvent);

        verify(kafkaTemplate, times(1)).send(
                TOPIC_NAME, likePostEvent);
    }
}

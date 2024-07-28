package faang.school.postservice.consumer.kafka;

import faang.school.postservice.service.cache.RedisCacheService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaLikeConsumerTest {
    @InjectMocks
    private KafkaLikeConsumer kafkaLikeConsumer;

    @Mock
    private RedisCacheService redisCacheService;

    private ConsumerRecord<Long, String> record;
    private Long postId;
    private String recordValue;

    @BeforeEach
    public void setUp() {
        record = Mockito.spy(new ConsumerRecord<>("topic", 0, 0L, 123L, "[{\"id\": 1}, {\"id\": 2}]"));
        recordValue = record.value();
        postId = record.key();
    }

    @Test
    public void testConsumerSuccess() {
        when(record.key()).thenReturn(postId);
        when(record.value()).thenReturn(recordValue);

        kafkaLikeConsumer.consume(record);

        verify(redisCacheService, times(1)).addLikeToCache(postId, recordValue);
    }
}

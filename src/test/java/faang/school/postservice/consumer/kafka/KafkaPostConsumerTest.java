package faang.school.postservice.consumer.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.service.cache.RedisCacheService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class KafkaPostConsumerTest {
    @InjectMocks
    private KafkaPostConsumer kafkaPostConsumer;

    @Mock
    private RedisCacheService redisCacheService;

    @Mock
    private ObjectMapper objectMapper;

    private ConsumerRecord<Long, String> record;

    @BeforeEach
    public void setUp() {
        record = new ConsumerRecord<>("topic", 0, 0L, 123L, "[{\"id\": 1}, {\"id\": 2}]");
    }

    @Test
    public void testConsumerSuccess() {
        JsonNode mockJsonNode = mock(JsonNode.class);
        JsonNode user1 = mock(JsonNode.class);
        JsonNode user2 = mock(JsonNode.class);

        try {
            when(objectMapper.readTree(record.value())).thenReturn(mockJsonNode);
            when(mockJsonNode.isArray()).thenReturn(true);
            when(mockJsonNode.iterator()).thenReturn(List.of(user1, user2).iterator());
            when(user1.get("id")).thenReturn(mock(JsonNode.class));
            when(user2.get("id")).thenReturn(mock(JsonNode.class));
            when(user1.get("id").asLong()).thenReturn(1L);
            when(user2.get("id").asLong()).thenReturn(2L);

            kafkaPostConsumer.consume(record);

            verify(redisCacheService, times(1)).addPostToUserFeed(123L, 1L);
            verify(redisCacheService, times(1)).addPostToUserFeed(123L, 2L);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testConsumerException() throws JsonProcessingException {
        doThrow(JsonProcessingException.class).when(objectMapper).readTree(record.value());
        assertThrows(RuntimeException.class, () -> kafkaPostConsumer.consume(record));
        verify(redisCacheService, never()).addPostToUserFeed(anyLong(), anyLong());
    }
}

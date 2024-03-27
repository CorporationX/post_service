package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEventDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;


@ExtendWith(MockitoExtension.class)
class KafkaPostProducerTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Spy
    private ObjectMapper objectMapper;
    @InjectMocks
    private KafkaPostProducer kafkaPostProducer;

    @Test
    void sendMessageSuccessTest() throws com.fasterxml.jackson.core.JsonProcessingException {
        PostEventDto event = PostEventDto.builder()
                .authorId(1L)
                .followers(List.of(2L, 3L))
                .build();
        ReflectionTestUtils.setField(kafkaPostProducer, "topic", "test-topic");
        String json = "{\"authorId\":1,\"followers\":[2,3]}";

        kafkaPostProducer.sendMessage(event);

        Mockito.verify(objectMapper, Mockito.times(1)).writeValueAsString(event);
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send("test-topic", json);
    }
}
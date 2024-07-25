package faang.school.postservice.publisher.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.user.UserFeedDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaPostPublisherTest {
    @InjectMocks
    private KafkaPostPublisher kafkaPostPublisher;

    @Mock
    private KafkaTemplate<Long, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.post}")
    private String postTopic = "test-topic";

    private List<UserFeedDto> userFeedDtoList;
    private Long postId;

    @BeforeEach
    public void setUp() {;
        kafkaPostPublisher.setPostTopic(postTopic);
        userFeedDtoList = List.of(new UserFeedDto(1L), new UserFeedDto(2L));
        postId = 12L;
    }

    @Test
    public void testSendMessageSuccess() throws JsonProcessingException {
        String userDtoListJson = objectMapper.writeValueAsString(userFeedDtoList);

        kafkaPostPublisher.sendMessage(userFeedDtoList, postId);

        verify(kafkaTemplate, times(1)).send(postTopic, postId, userDtoListJson);
    }

    @Test
    public void testSendMessageException() throws JsonProcessingException {
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(userFeedDtoList);

        assertThrows(RuntimeException.class, () -> kafkaPostPublisher.sendMessage(userFeedDtoList, postId));

        verify(kafkaTemplate, never()).send(anyString(), anyLong(), anyString());

    }

}

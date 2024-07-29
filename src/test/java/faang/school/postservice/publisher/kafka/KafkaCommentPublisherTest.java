package faang.school.postservice.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentFeedDto;
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
public class KafkaCommentPublisherTest {
    @InjectMocks
    private KafkaCommentPublisher kafkaCommentPublisher;

    @Mock
    private KafkaTemplate<Long, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.post}")
    private String commentTopic = "test-topic";

    private CommentFeedDto commentFeedDto;
    private Long postId;

    @BeforeEach
    public void setUp() {

        kafkaCommentPublisher.setCommentTopic(commentTopic);
        commentFeedDto = CommentFeedDto.builder().build();
        postId = 12L;
    }

    @Test
    public void testSendMessageSuccess() throws JsonProcessingException {
        String userDtoListJson = objectMapper.writeValueAsString(commentFeedDto);

        kafkaCommentPublisher.sendMessage(postId, commentFeedDto);

        verify(kafkaTemplate, times(1)).send(commentTopic, postId, userDtoListJson);
    }

    @Test
    public void testSendMessageException() throws JsonProcessingException {
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(commentFeedDto);

        assertThrows(RuntimeException.class, () -> kafkaCommentPublisher.sendMessage(postId, commentFeedDto));

        verify(kafkaTemplate, never()).send(anyString(), anyLong(), anyString());

    }
}
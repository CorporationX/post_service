package faang.school.postservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentFeedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaCommentPublisher {
    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.comment}")
    private String commentTopic;

    public void sendMessage(Long postId, CommentFeedDto commentFeedDto) {
        try {
            String commentFeedJson = objectMapper.writeValueAsString(commentFeedDto);
            kafkaTemplate.send(commentTopic, postId, commentFeedJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

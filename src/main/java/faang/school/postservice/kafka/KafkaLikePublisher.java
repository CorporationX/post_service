package faang.school.postservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaLikePublisher {
    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.like}")
    private String likeTopic;

    public void sendMessage(Long postId, LikeDto likeDtoSaved) {
        try {
            String likeJson = objectMapper.writeValueAsString(likeDtoSaved);
            kafkaTemplate.send(likeTopic, postId, likeJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

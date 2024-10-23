package faang.school.postservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.user.UserFeedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaPostPublisher {
    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.post}")
    private String postTopic;

    public void sendMessage(List<UserFeedDto> userDtoList, Long postId) {
        try {
            String userDtoListJson = objectMapper.writeValueAsString(userDtoList);
            kafkaTemplate.send(postTopic, postId, userDtoListJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}

package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaPostProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;

    public void send(String topic, PostDto postDto) throws JsonProcessingException {
        List<Long> subscribers = userServiceClient.getFollowers(postDto.getAuthorId()).stream()
                .map(UserDto::getId)
                .toList();

        kafkaTemplate.send(topic, String.valueOf(postDto.getAuthorId()), subscribers);
    }
}

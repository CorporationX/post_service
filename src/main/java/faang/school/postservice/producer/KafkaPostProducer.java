package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostKafkaDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;
    private final PostMapper postMapper;

    public void send(String topic, PostDto postDto) throws JsonProcessingException {
//        List<Long> subscribers = userServiceClient.getFollowers(postDto.getAuthorId()).stream()
//                .map(UserDto::getId)
//                .toList();
        List<Long> subscribers = List.of(1L,2L,3L,4L,5L,6L,7L,8L,9L);
        PostKafkaDto postKafkaDto = postMapper.toPostKafkaDto(postDto, subscribers);
        log.info("send post kafka: {}", postKafkaDto);
        String ms = objectMapper.writeValueAsString(postKafkaDto);
        log.info("send post kafka: {}", ms);
        kafkaTemplate.send(topic, ms);
    }
}

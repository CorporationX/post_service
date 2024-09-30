package faang.school.postservice.kafka_redis.kafka.producer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka_redis.kafka.model.PostKafkaModel;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;
    private final PostMapper postMapper;

    public void send(String topic, PostDto postDto) {
        List<Long> subscribers = userServiceClient.getFollowers(postDto.getAuthorId()).stream()
                .map(UserDto::getId)
                .toList();
        PostKafkaModel postModel = postMapper.toPostKafkaDto(postDto, subscribers);
        log.info("send post kafka: {}", postModel);
        kafkaTemplate.send(topic, postModel);
    }
}

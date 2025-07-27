package faang.school.postservice.publisher.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.dto.post.PostCreateEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer implements MessagePublisher<Post> {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;
    private final UserServiceClient userServiceClient;

    @Override
    public void publish(Post post) {
        if (!kafkaProperties.active()) {
            return;
        }

        try {
            List<Long> followerIds = userServiceClient.getFollowers(post.getAuthorId()).stream()
                    .map(UserDto::id)
                    .toList();
            PostCreateEvent postEvent = new PostCreateEvent(post.getId(), followerIds);
            kafkaTemplate.send(kafkaProperties.topicNames().posts(), objectMapper.writeValueAsString(postEvent));
            log.info("Message published in kafka. Created post id {}", post.getId());
        } catch (JsonProcessingException e) {
            log.error("Message not published in kafka. Can't convert post event to json [{}]: {}", post, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected exception on post creation [{}] message publishing in kafka.", post.getId(), e);
        }
    }
}

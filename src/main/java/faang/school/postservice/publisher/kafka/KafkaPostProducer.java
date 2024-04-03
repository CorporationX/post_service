package faang.school.postservice.publisher.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer extends AbstractKafkaProducer<Post> {

    private final UserServiceClient userServiceClient;

    @Value("${spring.kafka.topics.post.name}")
    private String postTopic;

    public void publishKafkaPostEvent(Post post) {
        List<Long> subscriberIds = userServiceClient.getAllFollowers(post.getAuthorId());
        KafkaPostEvent kafkaPostEvent = new KafkaPostEvent(post, subscriberIds);
        publishKafkaEvent(kafkaPostEvent.getPost(), postTopic);
    }

}
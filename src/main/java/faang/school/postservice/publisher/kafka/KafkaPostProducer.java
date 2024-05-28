package faang.school.postservice.publisher.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.events.PostPublicationEvent;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final KafkaTemplate<String, PostPublicationEvent> template;
    private final UserServiceClient userServiceClient;

    @Value("${spring.kafka.topics.post.publication}")
    private String topic;


    @Retryable(value = FeignException.FeignClientException.class)
    public void publish(Long followeeId) {

        userServiceClient.getFollowerIdsByFolloweeId(followeeId).ifPresent((followersId) -> {
            PostPublicationEvent event = PostPublicationEvent.builder()
                    .followersId(followersId)
                    .build();
            template.send(topic, event);
        });// should I add here a kafka key to make it coherent?
    }
}


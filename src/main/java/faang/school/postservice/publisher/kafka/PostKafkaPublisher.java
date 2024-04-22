package faang.school.postservice.publisher.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.dto.kafka.PostEventDto;
import feign.FeignException;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Component
public class PostKafkaPublisher extends AbstractKafkaPublisher<PostEventDto> {
    @Value("${kafka.producer.batch_size}")
    private int batchSize;
    @Value("${kafka.topics.post.name}")
    private String postTopic;
    private final UserServiceClient userServiceClient;

    public PostKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate, UserServiceClient userServiceClient) {
        super(kafkaTemplate);
        this.userServiceClient = userServiceClient;
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 5)
    public void publish(long postId, long ownerId, LocalDateTime publishedAt, KafkaKey kafkaKey) {
        List<Long> followers = userServiceClient.getFollowerIdsById(ownerId);

        ListUtils.partition(followers, batchSize).forEach(followersPartition -> {
                    PostEventDto postEventDto = PostEventDto.builder()
                            .postId(postId)
                            .authorId(ownerId)
                            .publishedAt(publishedAt)
                            .authorSubscriberIds(new HashSet<>(followersPartition))
                            .build();

                    send(postTopic, kafkaKey, postEventDto);
                }
        );
    }
}

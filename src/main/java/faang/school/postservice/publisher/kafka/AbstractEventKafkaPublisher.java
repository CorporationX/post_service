package faang.school.postservice.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Retryable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventKafkaPublisher<T> implements MessagePublisher<T> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final NewTopic topic;

    @Retryable(retryFor = FeignException.class, maxAttempts = 5)
    public void publish(T event) {
        try {
            kafkaTemplate.send(topic.name(), objectMapper.writeValueAsString(event));
            log.info("Comment event published: {}", event);
        } catch (JsonProcessingException e) {
            throw new SerializationException(e.getMessage());
        }
    }
//    private final UserServiceClient userServiceClient;
//
//    public PostKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate, UserServiceClient userServiceClient) {
//        super(kafkaTemplate);
//        this.userServiceClient = userServiceClient;
//    }
//
//    @Retryable(retryFor = FeignException.class, maxAttempts = 5)
//    public void publish(long postId, long ownerId, LocalDateTime publishedAt, KafkaKey kafkaKey) {
//        List<Long> followers = userServiceClient.getFollowerIdsById(ownerId);
//
//        ListUtils.partition(followers, batchSize).forEach(followersPartition -> {
//                    PostEventDto postEventDto = PostEventDto.builder()
//                            .postId(postId)
//                            .authorId(ownerId)
//                            .publishedAt(publishedAt)
//                            .authorSubscriberIds(new HashSet<>(followersPartition))
//                            .build();
//
//                    send(postTopic, kafkaKey, postEventDto);
//                }
//        );
//    }
}

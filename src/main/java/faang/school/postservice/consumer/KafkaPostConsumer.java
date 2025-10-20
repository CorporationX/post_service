package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.model.PostRedisEvent;
import faang.school.postservice.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "audit-kafka.enabled", havingValue = "true")
public class KafkaPostConsumer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisRepository redisRepository;

    @KafkaListener(
            topics = "${audit-kafka.topic}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${audit-kafka.consumer.group-id}"
    )
    public void consume(@Payload String postEvent) {
        PostEvent postEventObject = null;
        try {
            postEventObject = objectMapper.readValue(postEvent, PostEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("postEvent = " + postEventObject);


        postEventObject.getFollowersIds().forEach(followersId -> {
            redisRepository.findById(followersId).ifPresent(postRedisEvent -> {

            })
        })
        PostRedisEvent postRedisEvent = new PostRedisEvent(postEventObject.getFollowersIds(), new ArrayList<>(postEventObject.getPostId()));

        redisRepository.save()
    }
}

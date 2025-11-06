package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.model.PostRedisEvent;
import faang.school.postservice.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "post-kafka.enabled", havingValue = "true")
@Slf4j
public class KafkaPostConsumer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisRepository redisRepository;
    @Value(value = "${feed.max-size}")
    private int maxSize;


    @KafkaListener(
            topics = "${post-kafka.consumer.topic}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${post-kafka.consumer.group-id}"
    )
    public void consume(@Payload String postEvent,
                        Acknowledgment acknowledgment) {
        PostEvent postEventObject = null;
        try {
            postEventObject = objectMapper.readValue(postEvent, PostEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("postEvent = {}", postEventObject);

        Long postId = postEventObject.getPostId();
        postEventObject.getFollowersIds().forEach(followersId -> {
            Optional<PostRedisEvent> redisEvent = redisRepository.findById(followersId);
            Iterable<PostRedisEvent> all = redisRepository.findAllById(Collections.singletonList(followersId));
            List<PostRedisEvent> list = new ArrayList<>();
            all.forEach(e -> {
                list.add(e);
            });
            if (list.size() >= maxSize){
                list.remove(0);
                redisRepository.saveAll(list);
            }
            if (redisEvent.isPresent()) {
                    PostRedisEvent postRedisEvent = redisEvent.get();
                    Set<PostRedisEvent.PostEventData> postEventDataSet = postRedisEvent.getPostEventDataSet();
                    postEventDataSet.add(new PostRedisEvent.PostEventData(postId));
                    redisRepository.save(postRedisEvent);
            } else {
                PostRedisEvent postRedisEvent = new PostRedisEvent();
                postRedisEvent.setFollowerId(followersId);
                postRedisEvent.getPostEventDataSet().add(new PostRedisEvent.PostEventData(postId));
                redisRepository.save(postRedisEvent);
            }

        });
        acknowledgment.acknowledge();
    }


}

package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.LikeEventDto;
import faang.school.postservice.dto.post.PostRedisDto;
import faang.school.postservice.model.PostRedis;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "like-kafka.enabled", havingValue = "true")
@Slf4j
public class KafkaLikeConsumer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final RedisPostRepository redisPostRepository;

    @KafkaListener(
            topics = "likes",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "likes"
    )
    public void consume(@Payload String likeEventDto, Acknowledgment acknowledgment) {
        try {
            synchronized (this) {
                LikeEventDto likeEventObject = objectMapper.readValue(likeEventDto, LikeEventDto.class);
                long postId = likeEventObject.getPostId();
                Optional<PostRedis> postById = redisPostRepository.findById(postId);
                if (postById.isPresent()) {
                    PostRedisDto postRedisDto = postById.get().getPost().get(0);
                    postRedisDto.setLikes(postRedisDto.getLikes() + 1);
                    redisPostRepository.save(postById.get());
                } else {
                    throw new Error("Post not found");
                }
            }
            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}

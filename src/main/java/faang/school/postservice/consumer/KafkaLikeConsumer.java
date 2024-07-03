package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.LikeKafkaEvent;
import faang.school.postservice.model.redis.LikeRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final RedisPostRepository redisPostRepository;

    @KafkaListener(topics = "${spring.data.kafka.topics.likes.name}", groupId = "${spring.data.kafka.consumer.group-id}")
    public void listenLikeEvent(LikeKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Like event received. Author ID: {}, Post ID: {}", event.getAuthorId(), event.getPostId());
        PostRedis foundPost = redisPostRepository.findById(event.getPostId()).orElse(null);
        if (foundPost != null) {
            LikeRedis like = LikeRedis.builder()
                    .userId(event.getAuthorId())
                    .build();
            if (foundPost.getLikes() == null) {
                foundPost.setLikes(List.of(like));
            } else {
                foundPost.getLikes().add(like);
            }
            redisPostRepository.save(foundPost);
        }
        acknowledgment.acknowledge();
    }
}

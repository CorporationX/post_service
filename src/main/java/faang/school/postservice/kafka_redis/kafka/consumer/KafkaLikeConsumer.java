package faang.school.postservice.kafka_redis.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.kafka_redis.kafka.model.LikeKafkaModel;
import faang.school.postservice.kafka_redis.redis.model.PostRedisModel;
import faang.school.postservice.kafka_redis.redis.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {

    private RedisPostRepository repository;
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "likes", groupId = "group1")
    public void listener(String data) {
        log.info("Received message [{}] in group1", data);
        // посторался сделать так, что если придет дубликат с кафки, только один лайк попал в пост
        LikeKafkaModel likeKafkaModel = objectMapper.convertValue(data, LikeKafkaModel.class);
        PostRedisModel post = repository.findById(likeKafkaModel.getPostId()).orElseThrow();
        post.getLikes().add(likeKafkaModel);
        repository.save(post);
    }
}

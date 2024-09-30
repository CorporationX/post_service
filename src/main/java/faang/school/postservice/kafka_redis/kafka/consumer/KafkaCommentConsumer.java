package faang.school.postservice.kafka_redis.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.kafka_redis.kafka.model.CommentKafkaModel;
import faang.school.postservice.kafka_redis.redis.model.PostRedisModel;
import faang.school.postservice.kafka_redis.redis.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {

    @Value("${spring.data.kafka.topics.comment_topic.size:4}")
    private int topicSize;

    private RedisPostRepository repository;
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "likes", groupId = "group1")
    public void listener(String data) {
        log.info("Received message [{}] in group1", data);
        // посторался сделать так, что если придет дубликат с кафки, только один comment попал в пост
        CommentKafkaModel commentKafkaModel = objectMapper.convertValue(data, CommentKafkaModel.class);
        PostRedisModel post = repository.findById(commentKafkaModel.getPostId()).orElseThrow();
        int commentSize = post.getComments().size();
        if (commentSize > topicSize) {
            post.getComments().remove(0);
        }
        post.getComments().add(commentKafkaModel);
        repository.save(post);
    }
}

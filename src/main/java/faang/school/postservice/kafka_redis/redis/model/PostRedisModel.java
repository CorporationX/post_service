package faang.school.postservice.kafka_redis.redis.model;

import faang.school.postservice.kafka_redis.kafka.model.CommentKafkaModel;
import faang.school.postservice.kafka_redis.kafka.model.LikeKafkaModel;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.support.collections.RedisZSet;

import java.util.List;

@Data
@RedisHash("posts")
public class PostRedisModel {

    @Id
    private long postId;
    @NotNull
    private long authorId;
    private String content;
    private RedisZSet<LikeKafkaModel> likes;
    private List<CommentKafkaModel> comments;
}

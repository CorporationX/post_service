package faang.school.postservice.kafka_redis.kafka.model;

import lombok.Data;

@Data
public class LikeKafkaModel {
    private Long id;
    private Long postId;
    private Long authorLikeId;
}

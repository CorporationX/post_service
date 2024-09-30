package faang.school.postservice.kafka_redis.kafka.model;

import lombok.Data;

@Data
public class CommentKafkaModel {
    private Long id;
    private String content;
    private Long postId;
}

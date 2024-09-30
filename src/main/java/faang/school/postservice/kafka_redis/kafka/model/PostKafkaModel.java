package faang.school.postservice.kafka_redis.kafka.model;

import lombok.Data;

import java.util.List;

@Data
public class PostKafkaModel {

    private long id;
    private Long authorId;
    private String content;
    private List<Long> subscribers;
}

package faang.school.postservice.config.kafka.topic.params;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractKafkaTopicParams {
    private String name;
    private int partitions;
    private short replicationFactor;
}

package faang.school.postservice.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class HashtagRemovingTopicProperties {

    @Value("${spring.data.kafka.topic.hashtag-removing.name}")
    private String name;

    @Value("${spring.data.kafka.topic.hashtag-removing.partitions}")
    private int partitions;

    @Value("${spring.data.kafka.topic.hashtag-removing.replicas}")
    private int replicas;
}

package faang.school.postservice.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class HashtagAddingTopicProperties {

    @Value("${spring.data.kafka.topic.hashtag-adding.name}")
    private String name;

    @Value("${spring.data.kafka.topic.hashtag-adding.partitions}")
    private int partitions;

    @Value("${spring.data.kafka.topic.hashtag-adding.replicas}")
    private int replicas;
}

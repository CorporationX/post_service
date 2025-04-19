package faang.school.postservice.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class HashtagAddingTopicProperties {

    @Value("${spring.kafka.topics.hashtag-adding.name}")
    private String name;

    @Value("${spring.kafka.topics.hashtag-adding.partitions}")
    private int partitions;

    @Value("${spring.kafka.topics.hashtag-adding.replicas}")
    private int replicas;
}

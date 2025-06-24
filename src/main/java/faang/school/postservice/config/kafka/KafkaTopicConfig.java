package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.likes.name}")
    private String likesTopic;

    @Value("${spring.kafka.topic.likes.partition}")
    private int likesPartition;

    @Value("${spring.kafka.topic.likes.replicationFactor}")
    private short likesReplicationFactor;

    @Bean
    public NewTopic likesTopic() {
        return new NewTopic(likesTopic, likesPartition, likesReplicationFactor);
    }
}

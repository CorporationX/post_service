package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic commentTopic(@Value("${spring.kafka.topics.comment-topic.name}") String name,
                                 @Value("${spring.kafka.topics.comment-topic.num-partitions}") int partitions,
                                 @Value("${spring.kafka.topics.comment-topic.replication-factor}") short replicationFactor) {
        return new NewTopic(name, partitions, replicationFactor);
    }
}

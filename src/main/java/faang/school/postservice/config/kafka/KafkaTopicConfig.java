package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.topic.comment-topic}")
    private String commentTopic;

    @Value("${spring.kafka.partition.count}")
    private int partitionCount;

    @Value("${spring.kafka.streams.replication-factor}")
    private int replicationFactor;

    @Bean
    public NewTopic commentTopic() {
        return new NewTopic(commentTopic, partitionCount, (short) replicationFactor);
    }
}

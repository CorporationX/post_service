package faang.school.postservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfiguration {

    @Value("${spring.kafka.topics.user-ban-topic.name}")
    private String banUserTopicName;

    @Value("${spring.kafka.topics.user-ban-topic.num-partitions}")
    private int banUserNumPartitions;

    @Value("${spring.kafka.topics.user-ban-topic.replication-factor}")
    private short banUserReplicationFactor;

    @Bean
    public NewTopic createBanUserTopic() {
        return new NewTopic(banUserTopicName, banUserNumPartitions, banUserReplicationFactor);
    }
}

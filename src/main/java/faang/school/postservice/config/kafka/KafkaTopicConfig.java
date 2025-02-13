package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.topics.user-ban-topic.name}")
    private String userBanTopicName;

    @Value(value = "${spring.kafka.topics.user-ban-topic.num-partitions}")
    private int userBanTopicNumPartitions;

    @Value(value = "${spring.kafka.topics.user-ban-topic.replication-factor}")
    private short userBanTopicReplicationFactor;

    @Bean
    public NewTopic userBanTopic() {
        return new NewTopic(userBanTopicName, userBanTopicNumPartitions, userBanTopicReplicationFactor);
    }
}

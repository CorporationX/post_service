package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postTopic(
            @Value("${spring.kafka.topics.post.name}") String name,
            @Value("${spring.kafka.topics.post.partitions}") byte partitions
    ) {
        return new NewTopic(name, partitions, (short) 1);
    }

    @Bean
    public NewTopic newsFeedSubs(
            @Value("${spring.kafka.topics.news-feed-subs.name}") String name,
            @Value("${spring.kafka.topics.news-feed-subs.partitions}") byte partitions
    ) {
        return new NewTopic(name, partitions, (short) 1);
    }

    @Bean
    public NewTopic commentTopic(
            @Value("${spring.kafka.topics.comment.name}") String name,
            @Value("${spring.kafka.topics.comment.partitions}") byte partitions
    ) {
        return new NewTopic(name, partitions, (short) 1);
    }

    @Bean
    public NewTopic likeTopic(
            @Value("${spring.kafka.topics.like.name}") String name,
            @Value("${spring.kafka.topics.like.partitions}") byte partitions
    ) {
        return new NewTopic(name, partitions, (short) 1);
    }
}
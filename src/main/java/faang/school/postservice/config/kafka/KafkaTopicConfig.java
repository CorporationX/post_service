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
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.kafka.topics.post.name}")
    private String postTopic;
    @Value("${spring.kafka.topics.comment.name}")
    private String commentTopic;
    @Value("${spring.kafka.topics.like.name}")
    private String likeTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postTopic() {
        return new NewTopic(postTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic commentTopic() {
        return new NewTopic(commentTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic likeTopic() {
        return new NewTopic(likeTopic, 1, (short) 1);
    }
}
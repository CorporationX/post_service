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
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value("${spring.kafka.topic.new-post}")
    private String newPostTopicName;
    @Value("${spring.kafka.topic.like-post}")
    private String likePostTopicName;
    @Value("${spring.kafka.topic.comment-post}")
    private String commentPostTopicName;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic newPostTopic() {
        return new NewTopic(newPostTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic likePostTopic() {
        return new NewTopic(likePostTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic commonPostTopic() {
        return new NewTopic(commentPostTopicName, 1, (short) 1);
    }
}

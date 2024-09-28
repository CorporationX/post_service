package faang.school.postservice.config.topic;

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

    @Value(value = "${spring.data.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.data.kafka.topic.posts_topic}")
    private String postsTopic;

    @Value(value = "${spring.data.kafka.topic.comments_topic}")
    private String commentsTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic commentsTopic() {
        return new NewTopic(commentsTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic postsTopic() {
        return new NewTopic(postsTopic, 1, (short) 1);
    }
}

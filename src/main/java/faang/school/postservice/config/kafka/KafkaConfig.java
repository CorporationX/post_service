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
public class KafkaConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value(value = "${spring.kafka.topics.post}")
    private String topicPost;
    @Value(value = "${spring.kafka.topics.view}")
    private String topicView;
    @Value(value = "${spring.kafka.topics.like}")
    private String topicLike;
    @Value(value = "${spring.kafka.topics.comment}")
    private String topicComment;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topicPost() {
        return new NewTopic(topicPost, 1, (short) 1);
    }

    @Bean
    public NewTopic topicView() {
        return new NewTopic(topicView, 1, (short) 1);
    }

    @Bean
    public NewTopic topicLike() {
        return new NewTopic(topicLike, 1, (short) 1);
    }

    @Bean
    public NewTopic topicComment() {
        return new NewTopic(topicComment, 1, (short) 1);
    }
}

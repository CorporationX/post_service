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
    private String bootstrapServers;

    @Value("${spring.kafka.topics.post-publication}")
    private String postPublicationTopic;

    @Value("${spring.kafka.topics.like-publication}")
    private String likePublicationTopic;

    @Value("${spring.kafka.topics.comment-publication}")
    private String commentPublicationTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postViewTopic() {
        return new NewTopic("post-view", 1, (short) 1);
    }

    @Bean
    public NewTopic postPublicationTopic() {
        return new NewTopic(postPublicationTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic likePublicationTopic() {
        return new NewTopic(likePublicationTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic commentPublicationTopic() {
        return new NewTopic(commentPublicationTopic, 1, (short) 1);
    }
}

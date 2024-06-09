package faang.school.postservice.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Setter
@Getter
@ConfigurationProperties(prefix = "spring.data.kafka")
public class KafkaConfig {

    private String bootstrapServers;
    private String groupId;
    private String topicFeedHeat;
    private String topicPost;
    private String topicPostView;
    private String topicLike;
    private String topicComment;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public add topic

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {

    }

}

package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Setter
@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getAddress());
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic postKafkaTopic() {
        return TopicBuilder.name(kafkaProperties.getTopicsNames().getPostTopic())
                .compact()
                .build();
    }

    @Bean
    public NewTopic commentKafkaTopic() {
        return TopicBuilder.name("comments")
                .compact()
                .build();
    }
}

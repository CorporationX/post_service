package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaAdminConfig {

    @Value("${spring.data.kafka.host}")
    private String host;

    @Value("${spring.data.kafka.port}")
    private int port;

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%s", host, port));
        return new KafkaAdmin(configs);
    }

    @Bean
    public KafkaAdmin.NewTopics adminTopics(Map<String, NewTopic> topicMap) {
        return new KafkaAdmin.NewTopics(topicMap.values().toArray(NewTopic[]::new));
    }
}

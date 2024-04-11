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

    @Value("${spring.data.kafka.host}")
    private String host;

    @Value("${spring.data.kafka.port}")
    private int port;

    @Value("${spring.data.kafka.channels.comment-channel.name}")
    private String commentEventChannel;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, String.format("%s:%s", host, port));
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic commentEventTopic() {
        return new NewTopic(commentEventChannel, 1, (short) 1);
    }
}

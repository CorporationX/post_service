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
public class KafkaTopicConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        return new KafkaAdmin(configs);
    }

    @Bean(name = "postsTopic")
    public NewTopic postsTopic() {
        return new NewTopic(
                kafkaProperties.getTopic().getPost().getName(),
                kafkaProperties.getTopic().getPost().getPartitions(),
                kafkaProperties.getTopic().getPost().getReplicas()
                );
    }

    @Bean(name = "postViewsTopic")
    public NewTopic postViewsTopic() {
        return new NewTopic(
                kafkaProperties.getTopic().getPostView().getName(),
                kafkaProperties.getTopic().getPostView().getPartitions(),
                kafkaProperties.getTopic().getPostView().getReplicas()
        );
    }
}

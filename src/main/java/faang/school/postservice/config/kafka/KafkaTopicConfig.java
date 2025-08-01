package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.variables.partitions}")
    private int partitions;
    @Value("${spring.kafka.variables.replicas}")
    private int replicas;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic feedHeatEventTopic() {
        return TopicBuilder.name("feed_heat_event")
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic likeEventTopic() {
        return TopicBuilder.name("like_event")
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic commentEventTopic() {
        return TopicBuilder.name("comment_event")
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic postEventTopic() {
        return TopicBuilder.name("post_event")
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }

    @Bean
    public NewTopic postViewedEventTopic() {
        return TopicBuilder.name("post_viewed_event")
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
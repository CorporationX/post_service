package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.topic-name.likes:likes}")
    private String likesTopic;
    @Value("${spring.kafka.topic-name.post-views-topic}")
    private String postViewsTopic;
    @Value("${spring.kafka.topic-name.comments}")
    private String commentsTopic;
    @Value("${spring.kafka.topic-name.posts}")
    private String postsTopic;

    @Value("${spring.kafka.bootstrap_servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic likesTopic(){
        return TopicBuilder.name(likesTopic)
                .partitions(1)
                .replicas(1)
                .compact()
                .build();
    }
    @Bean
    public NewTopic postViewsTopic(){
        return TopicBuilder.name(postViewsTopic)
                .partitions(1)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic commentsTopic(){
        return TopicBuilder.name(commentsTopic)
                .partitions(1)
                .replicas(1)
                .compact()
                .build();
    }

    @Bean
    public NewTopic postTopic(){
        return TopicBuilder.name(postsTopic)
                .partitions(1)
                .replicas(1)
                .compact()
                .build();
    }
}

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
    @Value("${spring.kafka.bootstrap_servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.topic-name.likes:likes}")
    private String likesTopic;
    @Value("${spring.kafka.topic-name.post-views-topic:post_views}")
    private String postViewsTopic;
    @Value("${spring.kafka.topic-name.comments:comments}")
    private String commentsTopic;
    @Value("${spring.kafka.topic-name.posts:posts}")
    private String postsTopic;
    @Value("${spring.kafka.topic-name.heat-posts:heat_posts}")
    private String heatPostsTopic;
    @Value("${spring.kafka.topic-name.heat-feed:heat_feed}")
    private String heatFeedsTopic;
    @Value("${spring.kafka.topics.partitions}")
    private int partitionCount;
    @Value("${spring.kafka.topics.replicas}")
    private int replicaCount;

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic likesTopic(){
        return TopicBuilder.name(likesTopic)
                .partitions(partitionCount)
                .replicas(replicaCount)
                .compact()
                .build();
    }
    @Bean
    public NewTopic postViewsTopic(){
        return TopicBuilder.name(postViewsTopic)
                .partitions(partitionCount)
                .replicas(replicaCount)
                .compact()
                .build();
    }

    @Bean
    public NewTopic commentsTopic(){
        return TopicBuilder.name(commentsTopic)
                .partitions(partitionCount)
                .replicas(replicaCount)
                .compact()
                .build();
    }

    @Bean
    public NewTopic postTopic(){
        return TopicBuilder.name(postsTopic)
                .partitions(partitionCount)
                .replicas(replicaCount)
                .compact()
                .build();
    }

    @Bean
    public NewTopic heatPostsTopic(){
        return TopicBuilder.name(heatPostsTopic)
                .partitions(partitionCount)
                .replicas(replicaCount)
                .compact()
                .build();
    }

    public NewTopic heatFeedsTopic(){
        return TopicBuilder.name(heatFeedsTopic)
                .partitions(partitionCount)
                .replicas(replicaCount)
                .compact()
                .build();
    }
}
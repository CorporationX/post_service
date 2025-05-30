package faang.school.postservice.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    @Value("${spring.kafka.topics.likes.name}")
    private String likeName;
    @Value("${spring.kafka.topics.likes.partitions}")
    private int likePartitions;
    @Value("${spring.kafka.topics.likes.replicas}")
    private int likeReplicas;

    @Bean
    public NewTopic likesTopic() {
        return TopicBuilder.name(likeName)
                .partitions(likePartitions)
                .replicas(likeReplicas)
                .build();
    }
}

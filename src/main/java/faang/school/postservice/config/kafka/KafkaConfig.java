package faang.school.postservice.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topics.posts.name}")
    private String postsTopicName;

    @Value("${app.kafka.topics.posts.partitions}")
    private int postsTopicPartitions;

    @Value("${app.kafka.topics.post_views.name}")
    private String postViewsTopicName;

    @Value("${app.kafka.topics.post_views.partitions}")
    private int postViewsTopicPartitions;

    @Value("${app.kafka.topics.post_views.replication-factor}")
    private short postViewsTopicReplicationFactor;

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        log.info("KafkaTemplate configured successfully");
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic postViewsTopicConfig() {
        log.info("Creating Kafka topic: {} with {} partitions and replication factor: {}",
                postViewsTopicName, postViewsTopicPartitions, postViewsTopicReplicationFactor);

        return TopicBuilder.name(postViewsTopicName)
                .partitions(postViewsTopicPartitions)
                .replicas(postViewsTopicReplicationFactor)
                .config("cleanup.policy", "delete")
                .config("retention.ms", "259200000")
                .config("segment.ms", "43200000")
                .build();
    }
}

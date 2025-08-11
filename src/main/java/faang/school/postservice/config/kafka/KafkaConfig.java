package faang.school.postservice.config.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import lombok.RequiredArgsConstructor;

import org.apache.kafka.clients.admin.NewTopic;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaTopicsConfig kafkaTopicsConfig;

    @Bean
    public NewTopic profileViewTopic() {
        return TopicBuilder.name(kafkaTopicsConfig.getPostCreateEvent())
                .partitions(1)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic heatFeed() {
        return TopicBuilder.name(kafkaTopicsConfig.getHeatFeedRequest())
                .partitions(1)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic heatPost() {
        return TopicBuilder.name(kafkaTopicsConfig.getHeatPostRequest())
                .partitions(1)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic heatUser() {
        return TopicBuilder.name(kafkaTopicsConfig.getHeatUserRequest())
                .partitions(1)
                .replicas(3)
                .build();
    }
}

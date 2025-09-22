package faang.school.postservice.config.kafka;

import faang.school.postservice.config.properties.cache.feed.FeedWarmupProperties;
import faang.school.postservice.config.properties.kafka.FeedKafkaTopicsProperties;
import faang.school.postservice.config.properties.kafka.KafkaTopicsProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic createCommentTopic(KafkaTopicsProperties topics) {
        return TopicBuilder.name(topics.comment()).build();
    }

    @Bean
    public NewTopic feedPostsTopic(FeedKafkaTopicsProperties topics) {
        return TopicBuilder.name(topics.post()).build();
    }

    @Bean
    public NewTopic feedLikesTopic(FeedKafkaTopicsProperties topics) {
        return TopicBuilder.name(topics.like()).build();
    }

    @Bean
    public NewTopic feedWarmerTopic(FeedWarmupProperties feedWarmupProperties,
                                    FeedKafkaTopicsProperties topics) {
        return TopicBuilder.name(topics.warmer())
                .partitions(feedWarmupProperties.partitions())
                .build();
    }
}

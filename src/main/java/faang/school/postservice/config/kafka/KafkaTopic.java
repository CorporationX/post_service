package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {
    @Value("${spring.data.kafka.topics.post-channel}")
    private String postsTopic;

    @Value("${spring.data.kafka.topics.feed-channel}")
    private String feedTopic;

    @Bean
    public NewTopic postsTopic() {
        return TopicBuilder.name(postsTopic)
                .partitions(3)
                .build();
    }

    @Bean
    public NewTopic feedTopic() {
        return TopicBuilder.name(feedTopic)
                .partitions(3)
                .build();
    }
}

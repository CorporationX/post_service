package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {
    private final KafkaCommentTopicConfigurationProperties commentProps;
    @Bean
    public NewTopic commentTopic() {
        return TopicBuilder
                .name(commentProps.getName())
                .partitions(commentProps.getPartitions())
                .build();
    }
}
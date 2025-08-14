package faang.school.postservice.config.kafka;

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
}

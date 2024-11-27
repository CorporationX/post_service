package faang.school.postservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfiguration {

    private final KafkaProperties kafkaProperties;

    @Bean
    public NewTopic likeTopic() {
        return TopicBuilder.name(kafkaProperties.getTopics().getLike().getName()).build();
    }
}

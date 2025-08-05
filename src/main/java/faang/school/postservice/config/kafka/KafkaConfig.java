package faang.school.postservice.config.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.apache.kafka.clients.admin.NewTopic;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic profileViewTopic() {
        return TopicBuilder.name("post-create-event")
                .partitions(1)
                .replicas(1)
                .build();
    }

}

package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    @Value("${spring.data.kafka.topic.comment}")
    private String commentTopic;

    @Bean
    public NewTopic comment() {
        return TopicBuilder.name(commentTopic).partitions(2).build();
    }
}

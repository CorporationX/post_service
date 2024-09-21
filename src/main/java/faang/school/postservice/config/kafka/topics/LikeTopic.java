package faang.school.postservice.config.kafka.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LikeTopic {

    @Value("${spring.data.kafka.topics.like_topic}")
    private String likeTopic;

    @Bean
    public NewTopic likeTopic() {
        return new NewTopic(likeTopic, 1, (short) 1);
    }
}

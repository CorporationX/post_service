package faang.school.postservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.data.kafka.channels.comment-channel.name}")
    private String commentEventChannel;

    @Value("${spring.data.kafka.channels.post-channel.name}")
    private String postEventChannel;

    @Bean
    public NewTopic commentEventTopic() {
        return new NewTopic(commentEventChannel, 1, (short) 1);
    }

    @Bean
    public NewTopic postEventTopic() {
        return new NewTopic(postEventChannel, 1, (short) 1);
    }
}

package faang.school.postservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Value("${kafka.topic.ban_user}")
    private String banUserTopic;

    @Bean
    public NewTopic newTopic() {
        return new NewTopic(banUserTopic, 1, (short) 1);
    }
}

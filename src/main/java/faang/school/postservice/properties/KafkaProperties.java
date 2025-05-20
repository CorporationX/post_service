package faang.school.postservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.kafka.producer")
public class KafkaProperties {
    private String bootstrapServers;
    private Topic topic;

    @Getter
    @Setter
    public static class Topic {
        private String commentCreatedNotification;
    }
}

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
    private Topics topics;

    @Getter
    @Setter
    public static class Topics {
        private String commentCreatedNotification;
        private String likedPost;
    }
}

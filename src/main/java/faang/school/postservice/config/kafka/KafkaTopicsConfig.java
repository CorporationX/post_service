package faang.school.postservice.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "kafka.topics")
@Getter
@Setter
public class KafkaTopicsConfig {
    private String postCreateEvent;
    private String heatFeedRequest;
    private String heatPostRequest;
    private String heatUserRequest;
}

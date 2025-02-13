package faang.school.postservice.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
@ConfigurationProperties(prefix = "spring.data.kafka")
public record KafkaProperties(
        Map<String, String> channels) {
}

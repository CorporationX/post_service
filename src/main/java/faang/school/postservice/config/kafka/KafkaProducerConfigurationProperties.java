package faang.school.postservice.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.data.kafka.producer")
@Configuration
public class KafkaProducerConfigurationProperties {
    private String host;
    private int port;
}

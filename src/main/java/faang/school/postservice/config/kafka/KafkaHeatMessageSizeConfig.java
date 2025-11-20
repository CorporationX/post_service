package faang.school.postservice.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "heatpagesize")
@Getter
@Setter
public class KafkaHeatMessageSizeConfig {
    private int heatFeedPageSize;
    private int heatPostPageSize;
    private int heatUserPageSize;
}

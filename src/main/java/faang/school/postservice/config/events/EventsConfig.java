package faang.school.postservice.config.events;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EventsProperties.class)
public class EventsConfig {
}
package faang.school.postservice.config.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(value = "project-service")
public class ProjectService extends ApiProperties {
}

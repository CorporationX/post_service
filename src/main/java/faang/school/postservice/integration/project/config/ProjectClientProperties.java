package faang.school.postservice.integration.project.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.project-service")
public record ProjectClientProperties (
    @NotBlank
    String host,
    @Positive
    Integer port,
    @NotBlank
    String getProjectUrl
) {}

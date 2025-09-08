package faang.school.postservice.integration.user.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.user-service")
public record UserClientProperties (
        @NotBlank
        String host,
        @Positive
        Integer port,
        @NotBlank
        String getUserUrl
){

}

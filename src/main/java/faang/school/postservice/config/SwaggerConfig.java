package faang.school.postservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    private final ApiProperties apiProperties;

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info( new Info()
                        .title(apiProperties.getTitle())
                        .version(apiProperties.getVersion())
                        .description(apiProperties.getDescription()));
    }
}

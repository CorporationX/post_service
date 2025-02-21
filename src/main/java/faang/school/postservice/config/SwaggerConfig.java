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
                        .title("Post service API")
                        .version("0.1.0.1")
                        .description("Post service gives the ability to write text posts to share your thoughts, " +
                                "knowledge and information with other users."));
    }
}

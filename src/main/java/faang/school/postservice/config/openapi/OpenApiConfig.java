package faang.school.postservice.config.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Post Media Service API")
                        .version("1.0.0")
                        .description("API for managing posts, comments, and likes in the Post Service"))
                .servers(List.of(new io.swagger.v3.oas.models.servers.Server().url("/api/v1")));
    }
}

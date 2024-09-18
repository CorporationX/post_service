package faang.school.postservice.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * url: http://localhost:8081/v3/api-docs
 * http://localhost:8080/api-docs.yaml
 * http://localhost:8081/swagger-ui/index.html
 */
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI SwaggerApi() {
        return new OpenAPI()
                .info(new Info().title("Post service")
                        .description("сервис постов")
                        .version("1.0"));
    }
}

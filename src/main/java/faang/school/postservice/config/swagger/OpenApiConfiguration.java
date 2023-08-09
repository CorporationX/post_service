package faang.school.postservice.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//http://localhost:8081/swagger-ui/index.html#/
@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI postMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Post service API")
                        .description("Description of the methods for Post service")
                        .version("1.0"));
    }
}
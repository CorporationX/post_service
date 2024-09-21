package faang.school.postservice.config.swagger;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Post service API")
                        .version("1.0.0")
                        .description("Post service API")
                        .contact(new Contact()
                                .name("CorporationX")
                                .email("corpX.bc.com")))
                ;

    }
}
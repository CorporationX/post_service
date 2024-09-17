package faang.school.postservice.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Post Service API",
                description = "Post service api specification",
                version = "1.0.0",
                contact = @Contact(
                        name = "Faang School",
                        url = "https://faang-school.com/"
                ),
                license = @License(
                        name = "License name",
                        url = "https://some-url.com"
                ),
                termsOfService = "Terms of service"
        ),
        servers = @Server(
                url = "http://localhost:8081/",
                description = "Local Environment"))
public class SwaggerConfig {

}

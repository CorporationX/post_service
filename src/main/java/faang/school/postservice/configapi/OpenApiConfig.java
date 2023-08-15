package faang.school.postservice.configapi;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Post_service Api",
                description = "Post_service", version = "1.0.0",
                contact = @Contact(
                        name = "Myasnikov Ivan"
                )
        )
)
public class OpenApiConfig {
}

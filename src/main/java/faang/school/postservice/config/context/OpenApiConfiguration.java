package faang.school.postservice.config.context;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Corporation X - Post service \n Kelpie team. Stream 10",
                version = "1.0.0",
                description = "![Swagger logo](https://cdnb.artstation.com/p/assets/images/images/041/537/681/large/iva-trstenjak-image-from-ios-18.jpg?1632246798)"
        ))

public class OpenApiConfiguration {
}

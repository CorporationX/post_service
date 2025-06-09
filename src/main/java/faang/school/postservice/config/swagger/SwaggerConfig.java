package faang.school.postservice.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Post Service API")
                        .description("API for managing posts in the CorporationX system")
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement().addList("user-id-header"))
                .components(new Components()
                        .addSecuritySchemes("user-id-header",
                                new SecurityScheme()
                                        .name("x-user-id")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Enter authorized user id")
                        )
                );
    }
}

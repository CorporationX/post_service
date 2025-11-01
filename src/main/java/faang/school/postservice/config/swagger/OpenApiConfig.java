package faang.school.postservice.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        final String schemeName = "x-user-id";

        SecurityScheme headerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .name("x-user-id")
                .in(SecurityScheme.In.HEADER);

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(schemeName, headerAuth))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .info(new Info()
                        .title("Post Service API")
                        .description("CRUD и управление постами (создание, обновление, публикация, лайки, комментарии).")
                        .version("v1")
                        .contact(new Contact().name("Team CorporationX")));
    }
}
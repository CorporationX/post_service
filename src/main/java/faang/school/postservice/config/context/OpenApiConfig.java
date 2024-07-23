package faang.school.postservice.config.context;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info().title("Post server").version("v1"))
                .components(new Components()
                        .addParameters("x-user-id", new Parameter()
                                .name("x-user-id")
                                .in(ParameterIn.HEADER.toString())
                                .required(true)
                                .schema(new Schema<Long>().type("integer").format("int64"))
                        )
                );
    }
}
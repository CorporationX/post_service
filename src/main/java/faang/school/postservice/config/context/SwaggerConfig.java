package faang.school.postservice.config.context;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class SwaggerConfig {

    @Bean
    public OperationCustomizer addUserHeaderParameter() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            Parameter headerParameter = new Parameter()
                    .in("header")
                    .name("x-user-id")
                    .description("ID пользователя")
                    .required(true)
                    .schema(new StringSchema());

            operation.addParametersItem(headerParameter);
            return operation;
        };
    }
}
package faang.school.postservice.config.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
public abstract class ApiProperties {
    private String host;
    private String port;
    private List<String> requiredHeaders;
}

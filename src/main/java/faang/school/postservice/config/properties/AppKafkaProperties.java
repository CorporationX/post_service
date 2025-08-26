package faang.school.postservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.kafka")
public class AppKafkaProperties {

    private Topics topics = new Topics();

    @Getter
    @Setter
    public static class Topics {
        private String posts;
    }
}

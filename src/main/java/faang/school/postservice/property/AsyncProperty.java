package faang.school.postservice.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "async")
public class AsyncProperty {

    private final Map<String, AsyncSettings> settings;

    @Data
    public static class AsyncSettings {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
    }
}

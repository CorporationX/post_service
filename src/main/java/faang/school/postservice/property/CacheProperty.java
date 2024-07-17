package faang.school.postservice.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.cache")
public class CacheProperty {

    private Long defaultMaxSize;
    private Integer defaultTtl;
    private Map<String, CacheSettings> cacheSettings;

    @Data
    public static class CacheSettings {

        private String name;
        private Long maxSize;
        private Integer ttl;
    }
}

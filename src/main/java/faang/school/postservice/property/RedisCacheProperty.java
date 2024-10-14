package faang.school.postservice.property;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.data.redis.cache")
public class RedisCacheProperty {

    private final Long defaultTtl;
    private final String pathToEntities;
    private final Map<String, CacheSettings> cacheSettings;

    @Data
    public static class CacheSettings {

        private String name;
        private Long ttl;
    }

    @PostConstruct
    public void init() {
        cacheSettings.forEach((key, value) -> {
            if (value.getTtl() == null) {
                value.setTtl(defaultTtl);
            }
        });
    }
}

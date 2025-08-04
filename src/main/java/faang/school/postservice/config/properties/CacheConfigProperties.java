package faang.school.postservice.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@ConfigurationProperties(prefix = "cache.config")
@RequiredArgsConstructor
public class CacheConfigProperties {
    private final Duration defaultTtl;
    private final Duration authorsTtl;
}
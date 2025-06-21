package faang.school.postservice.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "executor-service")
@RequiredArgsConstructor
public class ExecutorServiceProperties {
    private final int threadsCount;
    private final int terminationTimeout;
}
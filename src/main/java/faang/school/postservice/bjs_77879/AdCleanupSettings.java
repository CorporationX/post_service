package faang.school.postservice.bjs_77879;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ad-cleanup")
public class AdCleanupSettings {

    private String cron;
    private int batchSize;

    private ThreadPoolProperties threadPool = new ThreadPoolProperties();

    @Data
    public static class ThreadPoolProperties {
        private int corePoolSize = 4;
        private int maxPoolSize = 8;
        private int queueCapacity = 100;
    }
}
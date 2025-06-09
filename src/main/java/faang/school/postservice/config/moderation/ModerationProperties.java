package faang.school.postservice.config.moderation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "moderation.comments")
@Getter
@Setter
public class ModerationProperties {
    private int batchSize;
    private int maxThreadPoolSize;
    private String cron;
}


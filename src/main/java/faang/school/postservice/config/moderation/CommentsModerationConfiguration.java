package faang.school.postservice.config.moderation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "moderation.comments")
@Getter
@Setter
@Component
public class CommentsModerationConfiguration {
    private String cron;
    private int batchSize;
    private int threadPoolSize;
    private int terminationAwait;
}

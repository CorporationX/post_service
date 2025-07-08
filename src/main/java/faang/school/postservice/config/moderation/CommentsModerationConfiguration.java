package faang.school.postservice.config.moderation;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "moderation.comments")
@Configuration
public class CommentsModerationConfiguration {
    private String cron;
    private int batchSize;
}

package faang.school.postservice.newsfeed.cron;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "news-feed.cron")
public class SchedulingConfig {
    private String OldEventIdsCleanUpCron;
    private int OldEventIdsCleanUpDays;
}

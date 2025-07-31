package faang.school.postservice.newsfeed.cron;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "project.news-feed")
public class SchedulingConfig {
    private String OldEventIdsCleanUpCron;
    private int OldEventIdsCleanUpDays;
}

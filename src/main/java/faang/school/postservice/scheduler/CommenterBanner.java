package faang.school.postservice.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CommenterBanner {

    @Scheduled(cron = "@midnight")
    public void scheduleCommentersBanCheck() {

    }
}

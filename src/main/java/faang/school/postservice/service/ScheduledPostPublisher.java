package faang.school.postservice.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface ScheduledPostPublisher {
    void publish();
}

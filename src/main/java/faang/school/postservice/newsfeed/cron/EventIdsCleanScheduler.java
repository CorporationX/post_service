package faang.school.postservice.newsfeed.cron;

import faang.school.postservice.newsfeed.repository.EventProcessedRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventIdsCleanScheduler {
    private final EventProcessedRepository repository;
    private final SchedulingConfig schedulingConfig;
    private final TaskScheduler scheduler;

    @PostConstruct
    public void schedule() {
        CronTrigger deleteOldRecordsTrigger = new CronTrigger(schedulingConfig.getOldEventIdsCleanUpCron());
        scheduler.schedule(this::deleteOldRecords, deleteOldRecordsTrigger);
    }

    //add @SchedulerLock
    private void deleteOldRecords() {
        repository.cleanUpOldRecords(schedulingConfig.getOldEventIdsCleanUpDays());
    }
}

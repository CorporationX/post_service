package faang.school.postservice.config;

import faang.school.postservice.service.post.ModerationJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class QuartzConfig {
    @Value("${quartz.job.interval-seconds}")
    private int intervalInSeconds;

    @Bean
    public JobDetail moderationJobDetail() {
        return JobBuilder.newJob(ModerationJob.class)
                .withIdentity("moderationJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger moderationJobTrigger(JobDetail moderationJobDetail) {
        SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                .withIntervalInSeconds(intervalInSeconds)
                .repeatForever();

        return TriggerBuilder.newTrigger()
                .forJob(moderationJobDetail)
                .withIdentity("moderationTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }
}
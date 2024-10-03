package faang.school.postservice.service.post;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ModerationJob implements Job {

    @Autowired
    private ModerationPostService moderationPostService;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting post moderation...");
        moderationPostService.moderateUnverifiedPosts();
        log.info("Post moderation completed.");
    }
}

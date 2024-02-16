package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ModerationScheduler {
    @Autowired
    private PostService postService;
    @Scheduled(cron = "${post.moderation.scheduler.cron}")
    public void moderation(){
        postService.checkingPostsWithBadWord();
    }

}

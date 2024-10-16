package faang.school.postservice.sheduler;

import faang.school.postservice.exception.post.ModeratingPostsException;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ModerationScheduler {

    private final PostService postService;

    @Scheduled(cron = "${post.moderator.scheduler.cron}")
    public void moderationScheduler() {
        try {
            postService.moderatePosts();
            log.info("Posts moderated successfully.");
        } catch (ModeratingPostsException ex) {
            log.error("Error while moderating posts: {}", ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Unexpected error during moderation: {}", ex.getMessage(), ex);
        }
    }
}

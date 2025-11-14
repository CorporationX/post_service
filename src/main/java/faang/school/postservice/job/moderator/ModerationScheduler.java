package faang.school.postservice.job.moderator;

import faang.school.postservice.service.moderate.ModerateComments;
import faang.school.postservice.service.post.PostV2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ModerationScheduler {

    private final ModerateComments moderateComments;
    private final PostV2Service postService;

    @Scheduled(fixedDelayString = "${app.moderation.comments.time:}")
    public void moderateComments() {
        moderateComments.moderateNewComments();
    }

    @Scheduled(cron = "${app.moderation.posts.cron}", zone = "Europe/Moscow")
    public void moderatePosts() {
        log.info("Starting moderation posts job");
        postService.moderatePosts();
        log.info("Finished moderation posts job");
    }
}

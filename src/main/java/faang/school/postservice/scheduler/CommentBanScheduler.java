package faang.school.postservice.scheduler;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentBanScheduler implements UserBanScheduler {

    private final CommentService commentService;

    @Scheduled(cron = "${task.scheduling.comment-ban.cron_expression}")
    @Override
    public void scheduleBan() {
        log.debug("Starting moderate comments");
        commentService.collectAndPushUsersForBan();
        log.debug("All comments moderated");
    }
}

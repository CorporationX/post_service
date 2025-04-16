package faang.school.postservice.scheduler;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentModerator implements Moderator {

    private final CommentService commentService;

    @Value("${spring.task.scheduling.comment.max_comments_per_size}")
    private int maxCommentsPerBatch;

    @Scheduled(cron = "${spring.task.scheduling.comment.cron_expression}")
    @Override
    public void startModerate() {
        log.debug("Starting moderate comments");
        commentService.moderateComments(maxCommentsPerBatch);
        log.debug("All comments moderated");
    }
}

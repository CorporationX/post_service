package faang.school.postservice.scheduler.comment;

import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final CommentService commentService;

    @Value("${spring.scheduler.comment.moderator.partition-size}")
    private Integer partitionSize;

    @Scheduled(cron = "${spring.scheduler.comment.moderator.cron}")
    public void moderateComments() {
        commentService.verifyComments(partitionSize);
    }
}

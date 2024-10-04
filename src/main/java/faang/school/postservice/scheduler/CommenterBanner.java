package faang.school.postservice.scheduler;

import faang.school.postservice.service.user.UserIdsPublisher;
import faang.school.postservice.service.comment.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommenterBanner {

    private final CommentServiceImpl commentService;
    private final UserIdsPublisher userIdsPublisher;

    private final int unverifiedCommentsLimit = 5;

    @Scheduled(cron = "@midnight")
    public void scheduleCommentersBanCheck() {
        Map<Long, Long> unverifiedCommentAuthorsAndComments = commentService.groupUnverifiedCommentAuthors(
                commentService.collectUnverifiedComments()
        );

        List<Long> usersToBan = unverifiedCommentAuthorsAndComments.entrySet().stream()
                .filter((longLongEntry -> longLongEntry.getValue() >= unverifiedCommentsLimit))
                .map((Map.Entry::getKey))
                .toList();


        log.info("Publishing User IDs to ban: {}", usersToBan);
        userIdsPublisher.publish(usersToBan);
    }
}

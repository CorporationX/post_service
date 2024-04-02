package faang.school.postservice.service.comment;

import faang.school.postservice.dto.event.UserEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.UserBanPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommenterBanner {

    private final CommentService commentService;
    private final UserBanPublisher userBanPublisher;

    @Value("${comment.commenter-banner.comments-count}")
    private Integer countCommentsForBan;

    @Scheduled(cron = "${comment.commenter-banner.scheduler.cron}")
    public void sendUsersToBan() {
        List<Comment> comments = commentService.findCommentsByVerified(false);
        Map<Long, Long> authorsCommsCount = comments.stream()
                .collect(Collectors.groupingBy(Comment::getAuthorId, Collectors.counting()));
        authorsCommsCount.forEach((authorId, commsCount) -> {
            if (commsCount > countCommentsForBan) {
                userBanPublisher.publish(new UserEvent(authorId));
                log.info("User {} has published to topic", authorId);
            }
        });
    }
}

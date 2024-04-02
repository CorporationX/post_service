package faang.school.postservice.service.comment;

import faang.school.postservice.dto.event.UserEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.UserBanPublisher;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final CommentRepository commentRepository;
    private final UserBanPublisher userBanPublisher;

    @Value("${comment.commenter-banner.comments-count}")
    private Integer countCommentsForBan;

    @Scheduled(fixedDelay = 1000L)
    public void sendUsersToBan() {
        List<Comment> comments = commentRepository.findByVerified(false);
        Map<Long, Long> authors = comments.stream()
                .collect(Collectors.groupingBy(Comment::getAuthorId, Collectors.counting()));
        authors.forEach((authorId, value) -> {
            if (value > countCommentsForBan) {
                userBanPublisher.publish(new UserEvent(authorId));
                System.out.println("published user " + authorId);
            }
        });
    }
}

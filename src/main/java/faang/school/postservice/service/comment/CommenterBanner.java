package faang.school.postservice.service.comment;

import faang.school.postservice.dto.event.UserEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.UserBanPublisher;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CommenterBanner {

    private final CommentRepository commentRepository;
    private final UserBanPublisher userBanPublisher;

    @Scheduled(fixedDelay = 10000L)
    public void sendUsersToBan() {
        List<Comment> comments = commentRepository.findByVerified(false);
        Map<Long, Long> authors = comments.stream()
                .collect(Collectors.groupingBy(Comment::getAuthorId, Collectors.counting()));
        authors.forEach((authorId, value) -> {
            if (value > 5) {
                userBanPublisher.publish(new UserEvent(authorId));
            }
        });
    }
}

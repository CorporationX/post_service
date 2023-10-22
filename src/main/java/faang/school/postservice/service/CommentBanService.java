package faang.school.postservice.service;

import faang.school.postservice.messaging.redis.publisher.BanEventPublisher;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentBanService {
    private final CommentService commentService;
    private final BanEventPublisher banEventPublisher;

    @Value("${comment.ban.numberOfCommentsToBan}")
    private int numberOfCommentsToBan;

    public void findCommentersAndPublishBanEvent() {
        List<Comment> unverifiedComments = commentService.getUnverifiedComments();

        Map<Long, List<Comment>> commentsByAuthor = unverifiedComments.stream()
                .collect(Collectors.groupingBy(Comment::getAuthorId));

        for (Map.Entry<Long, List<Comment>> entry : commentsByAuthor.entrySet()) {
            Long authorId = entry.getKey();
            List<Comment> authorComments = entry.getValue();

            if (authorComments.size() > numberOfCommentsToBan) {
                banEventPublisher.publishBanEvent(authorId);
            }
        }
    }
}

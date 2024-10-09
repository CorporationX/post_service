package faang.school.postservice.service.moderator;

import faang.school.postservice.config.dictionary.OffensiveWordsDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeratorService {

    private final CommentService commentService;
    private final ExecutorService executorService;
    private final OffensiveWordsDictionary offensiveWordsDictionary;

    public void moderateCommentsContent() {
        log.info("moderateCommentsContent() - start");
        List<Comment> comments = commentService.getUnverifiedComments();

        comments.forEach(comment -> executorService.execute(() -> {
            String content = comment.getContent();

            if (content != null && !content.isBlank()) {
                boolean noOffensiveContent = !containsOffensiveContent(content);
                setVerifyToComment(comment, noOffensiveContent);
            }
        }));

        commentService.saveComments(comments);
        log.info("moderateCommentsContent() - finish");
    }

    private boolean containsOffensiveContent(String content) {
        return Arrays.stream(content.toLowerCase().split("[\n\t.,; ]"))
                .parallel()
                .anyMatch(offensiveWordsDictionary::isWordContainsInDictionary);
    }

    public void setVerifyToComment(Comment comment, boolean isVerified) {
        comment.setVerified(isVerified);
        comment.setVerifiedAt(LocalDateTime.now());
    }
}

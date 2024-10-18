package faang.school.postservice.service.moderator;

import faang.school.postservice.config.dictionary.OffensiveWordsDictionary;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.comment.PublishedCommentEventPublisher;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeratorService {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final ExecutorService executorService;
    private final OffensiveWordsDictionary offensiveWordsDictionary;
    private final PublishedCommentEventPublisher publishedCommentEventPublisher;

    public void moderateCommentsContent() {
        log.info("moderateCommentsContent() - start");
        List<Comment> comments = commentService.getUnverifiedComments();

        CountDownLatch latch = new CountDownLatch(comments.size());

        comments.forEach(comment -> executorService.execute(() -> {
            try {
                String content = comment.getContent();

                if (content != null && !content.isBlank()) {
                    boolean noOffensiveContent = !containsOffensiveContent(content);
                    setVerifyToComment(comment, noOffensiveContent);
                }
            } finally {
                latch.countDown();
            }
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted", e);
        }

        commentService.saveComments(comments);
        notifyUserAboutNewComment(comments);

        log.info("moderateCommentsContent() - finish");
    }

    private void notifyUserAboutNewComment(List<Comment> comments) {
        comments.parallelStream()
                .filter(Comment::isVerified)
                .forEach(comment -> {
                    publishedCommentEventPublisher.publish(commentMapper.toCommentEventDto(comment));
                });
    }

    private boolean containsOffensiveContent(String content) {
        return Arrays.stream(content.toLowerCase().split("[\n\t.,; ]"))
                .parallel()
                .anyMatch(offensiveWordsDictionary::isWordContainsInDictionary);
    }

    private void setVerifyToComment(Comment comment, boolean isVerified) {
        comment.setVerified(isVerified);
        comment.setVerifiedAt(LocalDateTime.now());
    }
}

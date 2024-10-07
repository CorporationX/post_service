package faang.school.postservice.service.moderator;

import faang.school.postservice.config.dictionary.OffensiveWordsDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class ModeratorService {

    private final CommentService commentService;
    private final ExecutorService executorService;
    private final OffensiveWordsDictionary offensiveWordsDictionary;

    @Async("cachedExecutor")
    public CompletableFuture<Void> moderateCommentsContent() {
        return CompletableFuture.runAsync(() -> {
            List<Comment> comments = commentService.getUnverifiedComments();

            comments.forEach(comment -> executorService.execute(() -> {
                String content = comment.getContent();

                if (content != null && !content.isBlank()) {
                    boolean noOffensiveContent = !containsOffensiveContent(content);
                    commentService.setVerifyToComment(comment, noOffensiveContent);
                }
            }));

            commentService.saveComments(comments);
        }, executorService);
    }

    private boolean containsOffensiveContent(String content) {
        return Arrays.stream(content.toLowerCase().split("[\n\t.,; ]"))
                .parallel()
                .anyMatch(offensiveWordsDictionary::isWordContainsInDictionary);
    }
}

package faang.school.postservice.service.moderator;

import faang.school.postservice.config.dictionary.OffensiveWordsDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
public class ModeratorService {

    private final CommentService commentService;
    private final ExecutorService executorService;

    public ModeratorService(CommentService commentService,
                            @Qualifier("cachedExecutor") ExecutorService executorService) {
        this.commentService = commentService;
        this.executorService = executorService;
    }

    @Async("cachedExecutor")
    public CompletableFuture<Void> moderateCommentsContent() {
        return CompletableFuture.runAsync(() -> {
            List<Comment> comments = commentService.getUnverifiedComments();

            comments.forEach(comment -> executorService.execute(() -> {
                String content = comment.getContent();

                if (content != null && !content.isBlank()) {
                    boolean noOffensiveContent = !containsOffensiveContent(content);
                    commentService.verify(comment, noOffensiveContent);
                }
            }));
        }, executorService);
    }

    private boolean containsOffensiveContent(String content) {
        return Arrays.stream(content.toLowerCase().split("[\n\t.,; ]"))
                .parallel()
                .anyMatch(OffensiveWordsDictionary::isWordContainsInDictionary);
    }
}

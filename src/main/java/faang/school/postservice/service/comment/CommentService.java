package faang.school.postservice.service.comment;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ModerationDictionary moderationDictionary;
    @Value("${post.moderator.scheduler.batchSize}")
    private Integer batchSize;

    @Transactional
    public void moderateComment() {
        List<Comment> comments = commentRepository.findNotVerified();
        List<List<Comment>> grouped = new ArrayList<>();

        for (int i = 0; i < comments.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, comments.size());
            grouped.add(comments.subList(i, endIndex));
        }

        List<CompletableFuture<Void>> completableFutures = grouped.stream()
                .map(list -> CompletableFuture.runAsync(() -> verifyComment(list)))
                .toList();
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
    }

    private void verifyComment(List<Comment> comments) {
        Predicate<Comment> noUnwantedWords = comment -> !moderationDictionary.containsUnwantedWords(comment.getContent());
        Consumer<Comment> verifyComment = comment -> {
            comment.setVerified(true);
            comment.setVerifiedDate(LocalDateTime.now());
        };

        List<Comment> verifiedComments = comments.stream()
                .filter(noUnwantedWords)
                .peek(verifyComment)
                .toList();
        commentRepository.saveAll(verifiedComments);
    }
}

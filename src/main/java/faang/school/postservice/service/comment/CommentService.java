package faang.school.postservice.service.comment;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ModerationDictionary moderationDictionary;

    public void moderateComment() {
        Predicate<Comment> noUnwantedWords = comment -> !moderationDictionary.containsUnwantedWords(comment.getContent());
        Consumer<Comment> verifyComment = comment -> {
            comment.setVerified(true);
            comment.setVerifiedDate(LocalDateTime.now());
        };

        List<Comment> verifiedComments = commentRepository.findNotVerified().stream()
                .filter(noUnwantedWords)
                .peek(verifyComment)
                .toList();
        commentRepository.saveAll(verifiedComments);
    }
}

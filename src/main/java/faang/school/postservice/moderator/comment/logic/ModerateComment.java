package faang.school.postservice.moderator.comment.logic;

import faang.school.postservice.model.Comment;
import faang.school.postservice.moderator.comment.dictionary.ModerationDictionary;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ModerateComment {
    private final ModerationDictionary moderationDictionary;
    private final CommentRepository commentRepository;
    private final EntityManager entityManager;

    public void moderateComment(List<Comment> comments) {
        for (Comment comment : comments) {
            boolean containsBadWord = moderationDictionary.isContainsBadWordInTheText(comment.getContent());
            LocalDateTime verifiedDate = LocalDateTime.now();
            boolean verified = !containsBadWord;

            commentRepository.updateVerifiedAndVerifiedDate(comment.getId(), verified, verifiedDate);
        }

        entityManager.clear();
    }
}

package faang.school.postservice.moderator.comment.logic;

import faang.school.postservice.model.Comment;
import faang.school.postservice.moderator.dictionary.ModerationDictionary;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentModerator {
    private final ModerationDictionary moderationDictionary;
    private final CommentRepository commentRepository;
    private final EntityManager entityManager;

    public void moderateComment(List<Comment> comments) {
        log.debug("Starting moderation for {} comments", comments.size());

        for (Comment comment : comments) {
            boolean containsBadWord = moderationDictionary.isContainsBadWordInTheText(comment.getContent());
            LocalDateTime verifiedDate = LocalDateTime.now();
            boolean verified = !containsBadWord;

            commentRepository.updateVerifiedAndVerifiedDate(comment.getId(), verified, verifiedDate);
            log.debug("Comment ID: {} moderated. Contains bad word: {}. Verified: {}",
                    comment.getId(), containsBadWord, verified);
        }

        entityManager.clear();
        log.debug("Moderation completed and EntityManager cleared.");
    }
}

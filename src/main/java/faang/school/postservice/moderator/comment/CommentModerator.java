package faang.school.postservice.moderator.comment;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class CommentModerator {
    CommentRepository commentRepository;
    ModerationDictionary moderationDictionary;

    @Scheduled(cron = "0 0 8 * * *")
    public void verifyCommentsByDate(LocalDateTime verifiedDate) throws IOException {
        List<Comment> unverifiedComments = commentRepository.findCommentsByVerifiedDate(verifiedDate)
                .stream()
                .filter(comment -> !comment.isVerified())
                .toList();
        for (Comment comment : unverifiedComments) {
            moderationDictionary.verifyComment(comment);
        }
    }
}

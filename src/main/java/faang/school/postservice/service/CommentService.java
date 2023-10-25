package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ModerationDictionary moderationDictionary;

    public Comment findExistingComment(long commentId) {
        return commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }

    @Transactional(readOnly = true)
    public List<Comment> getUnverifiedComments() {
        return commentRepository.findByVerifiedDateBeforeAndVerifiedFalse(LocalDateTime.now());
    }

    @Transactional
    public void processCommentsBatch(List<Comment> comments) {
        for (Comment comment : comments) {
            boolean containsBannedWord = moderationDictionary.containsBannedWord(comment.getContent());
            comment.setVerified(!containsBannedWord);
            comment.setVerifiedDate(LocalDateTime.now());
            commentRepository.save(comment);
        }
    }


}

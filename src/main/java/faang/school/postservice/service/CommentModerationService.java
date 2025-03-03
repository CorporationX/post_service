package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.moderation.CommentDictionary;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentModerationService {

    private final CommentRepository commentRepository;
    private final CommentDictionary commentDictionary;

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsForModeration(int page, int size) {
        return commentRepository.findCommentsForModeration(PageRequest.of(page, size));
    }

    @Transactional
    public void processCommentsBatch(List<Comment> comments) {
        log.debug("Starting processing batch of {} comments", comments.size());
        LocalDateTime currentTime = LocalDateTime.now();

        comments.forEach(comment -> {
            try {
                if (comment.getContent() == null) {
                    log.warn("Comment ID: {} has null content", comment.getId());
                    return;
                }

                log.debug("Processing comment ID: {}", comment.getId());
                boolean containsBannedWord = commentDictionary.containsBannedWord(comment.getContent());
                log.debug("Comment ID: {} - containsBannedWord: {}", comment.getId(), containsBannedWord);

                boolean needsVerification = Boolean.FALSE.equals(comment.getVerified())
                        || (comment.getVerified() != null
                        && comment.getVerified()
                        && comment.getUpdatedAt() != null
                        && comment.getVerifiedDate() != null
                        && comment.getUpdatedAt().isAfter(comment.getVerifiedDate()));

                log.debug("Comment ID: {} - needsVerification: {}", comment.getId(), needsVerification);

                if (needsVerification) {
                    boolean newVerifiedStatus = !containsBannedWord;
                    boolean currentVerifiedStatus = Boolean.TRUE.equals(comment.getVerified());

                    log.debug("Comment ID: {} - current verified: {}, new: {}",
                            comment.getId(), currentVerifiedStatus, newVerifiedStatus);

                    if (currentVerifiedStatus != newVerifiedStatus) {
                        comment.setVerified(newVerifiedStatus);
                        comment.setVerifiedDate(currentTime);

                        if (containsBannedWord) {
                            log.info("BAN DETECTED | Comment ID: {} | Content: {}",
                                    comment.getId(), comment.getContent());
                        } else {
                            log.info("Comment APPROVED | ID: {}", comment.getId());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("ERROR processing comment ID: {}", comment.getId(), e);
            }
        });

        commentRepository.saveAll(comments);
        log.debug("Batch processing completed");
    }

}

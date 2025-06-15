package faang.school.postservice.moderation.model;

import faang.school.postservice.config.moderation.CommentsModerationConfiguration;
import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class CommentModeration {

    private final CommentService commentService;
    private final ModerationDictionary moderationDictionary;
    private final CommentsModerationConfiguration configuration;
    private final ThreadPoolTaskExecutor executor;

    @Scheduled(cron = "#{@commentsModerationConfiguration.cron}")
    @Transactional
    public void moderateComments() {
        List<List<Comment>> batchedCommentList = getCommentBatchesLockedForUpdate();

        batchedCommentList.forEach(batch -> executor.submit(() -> verifyComments(batch)));
    }

    @Scheduled(cron = "#{@commentsModerationConfiguration.cron}")
    public void deleteCommentsWithProfanities() {
        int deletedComments = commentService.deleteCommentsWithProfanities();
        log.info("Deleted {} number of comments with profanities", deletedComments);
    }

    private List<Comment> verifyComments(List<Comment> comments) {
        log.info("Moderation started. Batch size: {}", comments.size());

        List<Comment> verified = comments.stream()
                .peek(comment -> {
                    boolean hasProfanity = moderationDictionary.containsProfanity(comment.getContent());
                    comment.setVerified(!hasProfanity);
                    comment.setVerifiedDate(LocalDateTime.now());
                    log.debug("Comment {} verification result: {}", comment.getId(), !hasProfanity);
                })
                .toList();

        log.info("Moderation finished. Verified comments: {}", verified.size());
        return commentService.saveVerifiedComments(verified);
    }

    private List<List<Comment>> getCommentBatchesLockedForUpdate() {
        List<Comment> comments = commentService.getNotVerifiedComments();
        int batchSize = configuration.getBatchSize();

        return ListUtils.partition(comments, batchSize);
    }
}

package faang.school.postservice.job.comment;

import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.config.moderation.ModerationProperties;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.utils.GracefullyShutdownThreadPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Component
@Slf4j
public class CommentModerationJob {

    private final CommentService commentService;
    private final ModerationDictionary moderationDictionary;
    private final ModerationProperties moderationProperties;

    @Scheduled(cron = "#{@moderationProperties.cron}")
    public void moderateCommentsToOffensiveContent() {
        log.info("Начата модерация комментариев");

        List<List<Comment>> batches = getCommentBatches();
        ExecutorService executorService = Executors.newFixedThreadPool(moderationProperties.getMaxThreadPoolSize());

        try {
            batches.forEach(batch -> executorService.submit(() -> processBatch(batch)));
        } finally {
            GracefullyShutdownThreadPool.gracefullyShutdown(executorService);
        }

        log.info("Завершена модерация комментариев");
    }

    private void processBatch(List<Comment> batch) {
        try {
            List<Comment> verifiedComments = new ArrayList<>();
            for (Comment comment : batch) {
                boolean offensive = moderationDictionary.containsOffensive(comment.getContent());
                if (offensive) {
                    commentService.delete(comment.getId());
                } else {
                    comment.setVerified(true);
                    comment.setVerifiedDate(LocalDateTime.now());
                    verifiedComments.add(comment);
                }
            }

            if (!verifiedComments.isEmpty()) {
                commentService.saveAll(verifiedComments);
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке батча комментариев", e);
        }
    }

    private List<List<Comment>> getCommentBatches() {
        List<Comment> comments = commentService.getUnverifiedComments();
        List<List<Comment>> batches = new ArrayList<>();
        int batchSize = moderationProperties.getBatchSize();

        for (int i = 0; i < comments.size(); i += batchSize) {
            batches.add(comments.subList(i, Math.min(i + batchSize, comments.size())));
        }

        return batches;
    }
}

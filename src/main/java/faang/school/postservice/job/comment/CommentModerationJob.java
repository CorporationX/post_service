package faang.school.postservice.job.comment;

import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.config.moderation.ModerationProperties;
import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class CommentModerationJob {

    private final CommentService commentService;
    private final ModerationDictionary moderationDictionary;
    private final ModerationProperties moderationProperties;
    private final Executor executor;

    public CommentModerationJob(CommentService commentService,
                                ModerationDictionary moderationDictionary,
                                ModerationProperties moderationProperties,
                                @Qualifier("correctComments") Executor executor) {
        this.commentService = commentService;
        this.moderationDictionary = moderationDictionary;
        this.moderationProperties = moderationProperties;
        this.executor = executor;
    }

    @Scheduled(cron = "#{@moderationProperties.cron}")
    public void moderateCommentsToOffensiveContent() {
        log.info("Начата модерация комментариев");

        List<Comment> comments = commentService.fetchCommentsForModeration(moderationProperties.getBatchSize());
        List<List<Comment>> batches = ListUtils.partition(comments, moderationProperties.getBatchSize());

        batches.forEach(batch -> executor.execute(() -> processBatch(batch)));

        log.info("Завершена модерация комментариев");
    }

    private void processBatch(List<Comment> batch) {
        batch.forEach(comment -> {
            if (moderationDictionary.containsOffensive(comment.getContent())) {
                commentService.delete(comment.getId());
            } else {
                comment.setVerified(true);
                commentService.save(comment);
            }
        });
    }
}

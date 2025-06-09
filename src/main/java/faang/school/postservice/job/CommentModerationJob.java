package faang.school.postservice.job;

import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.utils.GracefullyShutdownThreadPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${moderation.comments.batch-size}")
    private int batchSize;

    @Value("${moderation.comments.max-thread-pool-size}")
    private int maxThreadPoolSize;

    @Scheduled(cron = "${moderation.comments.cron}")
    public void moderateCommentsToOffensiveContent() {
        log.info("Started checking profanities for comments");

        List<List<Comment>> batches = getCommentBatches();
        ExecutorService executorService = Executors.newFixedThreadPool(maxThreadPoolSize);
        try {
            batches.forEach(batch ->
                    executorService.submit(() -> {
                        List<Comment> verifiedComments = new ArrayList<>();
                        for (Comment comment : batch) {
                            boolean offensive = moderationDictionary.containsOffensive(comment.getContent());
                            if (offensive) {
                                commentService.delete(comment.getId());
                            }
                            else {
                                comment.setVerified(true);
                                comment.setVerifiedDate(LocalDateTime.now());

                                verifiedComments.add(comment);
                            }
                        }

                        commentService.saveAll(verifiedComments);
                    })
            );
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            GracefullyShutdownThreadPool.gracefullyShutdown(executorService);
        }

        log.info("Finished checking profanities for comments");
    }

    private List<List<Comment>> getCommentBatches() {
        List<Comment> comments = commentService.getUnverifiedComments();

        List<List<Comment>> batches = new ArrayList<>();
        for (int i = 0; i < comments.size(); i += batchSize) {
            batches.add(comments.subList(i, i + batchSize));
        }

        return batches;
    }
}

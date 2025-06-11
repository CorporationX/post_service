package faang.school.postservice.moderation.model;

import faang.school.postservice.config.moderation.CommentsModerationConfiguration;
import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
@Slf4j
public class CommentModeration {

    private final CommentService commentService;
    private final ModerationDictionary moderationDictionary;
    private final CommentsModerationConfiguration configuration;

    @Scheduled(cron = "#{@commentsModerationConfiguration.cron}")
    public void moderateComments() {
        log.info("Moderation started");

        List<List<Comment>> batchedCommentList = getBatches();
        ExecutorService executorService = Executors.newFixedThreadPool(configuration.getThreadPoolSize());

        batchedCommentList.forEach(batch -> executorService.submit(() -> verifyComments(batch)));

        executorShutdown(executorService);

        log.info("Moderation finished");
    }

    @Scheduled(cron = "#{@commentsModerationConfiguration.cron}")
    public void deleteCommentsWithProfanities() {
        log.info("Deleting comments with profanities");
        commentService.deleteCommentsWithProfanities();
    }

    private List<Comment> verifyComments(List<Comment> comments) {
        List<Comment> verified = comments.stream()
                .peek(comment -> {
                    boolean hasProfanity = moderationDictionary.containsProfanity(comment.getContent());
                    comment.setVerified(!hasProfanity);
                    comment.setVerifiedDate(LocalDateTime.now());
                    log.debug("Comment {} verification result: {}", comment.getId(), !hasProfanity);
                })
                .toList();

        return commentService.saveVerifiedComments(verified);
    }

    private List<List<Comment>> getBatches() {
        List<Comment> comments = commentService.getNotVerifiedComments();
        int batchSize = configuration.getBatchSize();
        List<List<Comment>> batchedCommentList = new ArrayList<>();

        IntStream.iterate(0, i -> i < comments.size(), i -> i + batchSize)
                .forEach(i -> batchedCommentList.add(comments.subList(i, Math.min(i + batchSize, comments.size()))));

        return batchedCommentList;
    }

    private void executorShutdown(ExecutorService service) {
        service.shutdown();

        try {
            if (!service.awaitTermination(configuration.getTerminationAwait(), TimeUnit.MINUTES)) {
                log.info("Forcing shutdown");
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
        }
    }
}

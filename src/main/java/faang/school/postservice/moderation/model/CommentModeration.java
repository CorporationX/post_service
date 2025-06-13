package faang.school.postservice.moderation.model;

import faang.school.postservice.config.executor.ConfiguredExecutorService;
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
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Component
@Slf4j
public class CommentModeration {

    private final CommentService commentService;
    private final ModerationDictionary moderationDictionary;
    private final CommentsModerationConfiguration configuration;
    private final ConfiguredExecutorService executorService;

    @Scheduled(cron = "#{@commentsModerationConfiguration.cron}")
    public void moderateComments() {
        List<List<Comment>> batchedCommentList = getBatches();

        batchedCommentList.forEach(batch -> executorService.taskExecutor().submit(() -> verifyComments(batch)));
    }

    @Scheduled(cron = "#{@commentsModerationConfiguration.cron}")
    public void deleteCommentsWithProfanities() {
        int deletedComments = commentService.deleteCommentsWithProfanities();
        log.info("Deleted {} number of commetns fith profanities", deletedComments);
    }

    private List<Comment> verifyComments(List<Comment> comments) {
        log.info("Moderation started. Batch size = {}", comments.size());
        long start = System.currentTimeMillis();
        List<Comment> verified = comments.stream()
                .peek(comment -> {
                    boolean hasProfanity = moderationDictionary.containsProfanity(comment.getContent());
                    comment.setVerified(!hasProfanity);
                    comment.setVerifiedDate(LocalDateTime.now());
                    log.debug("Comment {} verification result: {}", comment.getId(), !hasProfanity);
                })
                .toList();
        long end = System.currentTimeMillis();

        log.info("Moderation finished. Execution time: {} ms, ", end - start);
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
}

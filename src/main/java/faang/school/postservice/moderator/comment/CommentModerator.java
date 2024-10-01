package faang.school.postservice.moderator.comment;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

;

@Component
public class CommentModerator {
    @Value("${comment.moderation.batch-size}")
    private int batchSize;
    CommentRepository commentRepository;
    ModerationDictionary moderationDictionary;

    @Bean
    public ExecutorService moderatorPool() {
        return Executors.newFixedThreadPool(10);
    }

    @Async("moderatorPool")
    @Scheduled(cron = "0 0 8 * * *")
    public void verifyCommentsByDate(LocalDateTime verifiedDate) throws IOException, ExecutionException, InterruptedException {
        List<Comment> unverifiedComments = commentRepository.findCommentsByVerifiedDate(verifiedDate)
                .stream()
                .filter(comment -> !comment.isVerified())
                .toList();

        List<List<Comment>> sublists = Lists.partition(unverifiedComments, batchSize);

        for (List<Comment> sublist : sublists) {
            for (Comment comment : sublist) {
                moderationDictionary.verifyComment(comment);
            }
        }
    }
}

package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final ModerationDictionary moderationDictionary;

    @Value("${moderation.chunk-size}")
    private int chunkSize;

    @Transactional
    public void moderateUnverifiedComments() {
        List<Comment> comments = commentRepository.findByVerifiedIsNull();

        if (comments.isEmpty()) {
            log.info("No comments to moderate.");
            return;
        }

        log.info("Found {} unverified comments to process", comments.size());

        List<List<Comment>> chunks = new ArrayList<>();
        for (int i = 0; i < comments.size(); i += chunkSize) {
            chunks.add(comments.subList(i, Math.min(i + chunkSize, comments.size())));
        }

        ExecutorService executor = Executors.newFixedThreadPool(chunks.size());

        for (List<Comment> chunk : chunks) {
            executor.submit(() -> {
                for (Comment comment : chunk) {
                    boolean hasBadWords = moderationDictionary.containsBadWords(comment.getContent());
                    comment.setVerified(!hasBadWords);
                    comment.setVerifiedDate(LocalDateTime.now());
                }
                commentRepository.saveAll(chunk);
            });
        }
        executor.shutdown();

        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                log.warn("Executor did not terminate in the specified time.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Executor was interrupted: {}", e.getMessage());
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        log.info("Moderation finished.");
    }
}

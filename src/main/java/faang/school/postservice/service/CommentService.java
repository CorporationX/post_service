package faang.school.postservice.service;

import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.moderation.ModerationDictionary;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ModerationDictionary moderationDictionary;

    @Value("${comment.batch.size}")
    private int batchSize;

    public void moderateComments() {
        log.info("Comment moderation start");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<Comment> unverifiedComments = commentRepository.findUnverifiedComments();

        for (int i = 0; i < unverifiedComments.size(); i += batchSize) {
            List<Comment> batch = unverifiedComments.subList(i, Math.min(i + batchSize, unverifiedComments.size()));
            futures.add(CompletableFuture.runAsync(() -> batch.forEach(moderationDictionary::checkComment)));
        }
        commentRepository.saveAll(unverifiedComments);
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("Comment moderation has been completed");
    }

    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException(String
                        .format("Comment with id:%d doesn't exist", commentId)));
    }
}

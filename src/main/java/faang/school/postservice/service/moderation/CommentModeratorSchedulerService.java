package faang.school.postservice.service.moderation;

import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class CommentModeratorSchedulerService {
    private final CommentService commentService;
    private final ModerationDictionary moderationDictionary;

    @Value("${comment.batch.size}")
    private int batchSize;

    @Scheduled(cron = "${comment.moderator.scheduler.cron}")
    public void moderateComments() {
        log.info("Comment moderation start");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<Comment> unverifiedComments = commentService.findUnverifiedComments();

        for (int i = 0; i < unverifiedComments.size(); i += batchSize) {
            List<Comment> batch = unverifiedComments.subList(i, Math.min(i + batchSize, unverifiedComments.size()));
            futures.add(CompletableFuture.runAsync(() -> batch.forEach(moderationDictionary::checkComment)));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        commentService.saveAll(unverifiedComments);
        log.info("Comment moderation has been completed");
    }
}

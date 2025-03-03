package faang.school.postservice.moderation;

import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentModerator {

    private final CommentModerationService commentModerationService;

    @Qualifier("taskExecutor")
    private final ThreadPoolTaskExecutor taskExecutor;

    @Value("${comment.moderation.batchSize}")
    private int batchSize;

    @Value("${comment.moderation.pageSize}")
    private int pageSize;

    @Scheduled(cron = "${comment.moderation.cron}")
    public void moderateComments() {
        IntStream.range(0, Integer.MAX_VALUE)
                .mapToObj(page -> commentModerationService.getCommentsForModeration(page, pageSize))
                .takeWhile(page -> !page.isEmpty())
                .forEach(page -> processPage(page.getContent()));
    }

    private void processPage(List<Comment> comments) {
        IntStream.iterate(0, i -> i < comments.size(), i -> i + batchSize)
                .mapToObj(i -> comments.subList(i, Math.min(i + batchSize, comments.size())))
                .forEach(batch ->
                        taskExecutor.submit(() ->
                                commentModerationService.processCommentsBatch(batch)
                        )
                );
    }
}
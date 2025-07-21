package faang.school.postservice.moderation.model.post;

import faang.school.postservice.config.moderation.CommentsModerationConfiguration;
import faang.school.postservice.service.post.moderation.PostModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;


@Component
@Slf4j
public class PostModerationJob {

    private final ThreadPoolTaskExecutor executor;
    private final PostModerationService postModerationService;
    private final Integer batchSize;

    public PostModerationJob(@Qualifier("taskExecutor") ThreadPoolTaskExecutor executor,
                             PostModerationService postModerationService,
                             CommentsModerationConfiguration configuration) {
        this.executor = executor;
        this.postModerationService = postModerationService;
        this.batchSize = configuration.getBatchSize();
    }

    @Scheduled(cron = "#{@commentsModerationConfiguration.cron}")
    public void moderatePosts() {
        int totalCount = postModerationService.countNotVerifiedPosts();
        int totalBatches = (int) Math.ceil((double) totalCount / batchSize);

        log.info("Starting posts moderation of {} batches with {} posts in each", totalBatches, batchSize);
        IntStream.rangeClosed(1, totalBatches)
                .forEach(i -> executor.submit(() -> postModerationService.verifyPostsBatch(batchSize)));
    }
}
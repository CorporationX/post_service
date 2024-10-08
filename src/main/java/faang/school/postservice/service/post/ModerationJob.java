package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class ModerationJob implements Job {
    private final ModerationPostService moderationService;

    @Value("${moderation.sublist-size}")
    private int sublistSize;

    @Value("${moderation.thread-pool-size}")
    private int threadPoolSize;

    @Value("${moderation.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting post moderation job...");

        List<Post> unverifiedPosts = moderationService.findUnverifiedPosts();
        log.info("Found {} unverified posts", unverifiedPosts.size());

        List<List<Post>> sublists = moderationService.splitListIntoSublists(unverifiedPosts, sublistSize);
        log.info("Split posts into {} sublists", sublists.size());

        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

        try {
            for (List<Post> sublist : sublists) {
                executorService.submit(() -> moderationService.moderatePostsSublist(sublist));
            }
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(awaitTerminationSeconds, TimeUnit.SECONDS)) {
                    log.warn("Executor service did not terminate in the specified time.");
                    List<Runnable> droppedTasks = executorService.shutdownNow();
                    log.warn("Dropped {} tasks", droppedTasks.size());
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        log.info("Post moderation job completed.");
    }
}
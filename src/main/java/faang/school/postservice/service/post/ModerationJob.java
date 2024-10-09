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

@Slf4j
@RequiredArgsConstructor
@Component
public class ModerationJob implements Job {
    private final ModerationPostService moderationService;

    @Value("${moderation.sublist-size}")
    private int sublistSize;

    @Value("${moderation.thread-pool-size}")
    private int threadPoolSize;

    private final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting post moderation job...");

        List<Post> unverifiedPosts = moderationService.findUnverifiedPosts();
        log.info("Found {} unverified posts", unverifiedPosts.size());

        List<List<Post>> sublists = moderationService.splitListIntoSublists(unverifiedPosts, sublistSize);
        log.info("Split posts into {} sublists", sublists.size());

        sublists.forEach(sublist ->
                executorService.submit(() -> moderationService.moderatePostsSublist(sublist))
        );
        log.info("Post moderation job completed.");
    }
}
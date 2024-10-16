package faang.school.postservice.scheduler;

import faang.school.postservice.model.entity.Post;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationScheduler {

    private final PostService postService;

    @Scheduled(cron = "${post.moderation.scheduler.cron}")
    public void verifyPosts() {
        String currentTime;
        List<List<Post>> unverifiedPosts = postService.findAndSplitUnverifiedPosts();

        if (!unverifiedPosts.isEmpty()) {
            currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("Starting post moderation process for {} batches at {}", unverifiedPosts.size(), currentTime);

            List<CompletableFuture<Void>> futures = unverifiedPosts.stream()
                    .map(postService::verifyPostsForSwearWords)
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            log.info("Completed moderation process for all post batches.");
        } else {
            currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("No unverified posts found at {}", currentTime);
        }
    }
}

package faang.school.postservice.scheduler;

import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.RateDecreaseEvent;
import faang.school.postservice.publisher.RateDecreaseEventPublisher;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationScheduler {

    private final PostService postService;
    private final RateDecreaseEventPublisher rateDecreaseEventPublisher;

    @Scheduled(cron = "${post.moderation.scheduler.cron}")
    public void verifyPosts() {
        String currentTime;
        List<List<Post>> unverifiedPosts = postService.findAndSplitUnverifiedPosts();

        if (!unverifiedPosts.isEmpty()) {
            currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("Starting post moderation process for {} batches at {}", unverifiedPosts.size(), currentTime);

            Set<Long> allUsersWithImproperContent = Collections.synchronizedSet(new HashSet<>());

            List<CompletableFuture<Void>> futures = unverifiedPosts.stream()
                    .map(batch -> postService.verifyPostsForSwearWords(batch, allUsersWithImproperContent))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            if (!allUsersWithImproperContent.isEmpty()) {
                List<Long> userIds = new ArrayList<>(allUsersWithImproperContent);

                log.info("Publishing RateDecreaseEvent for users who wrote posts with improper content: {}", userIds);

                rateDecreaseEventPublisher.publish(new RateDecreaseEvent("expletives", userIds));
            }

            log.info("Completed moderation process for all post batches.");
        } else {
            currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("No unverified posts found at {}", currentTime);
        }
    }
}

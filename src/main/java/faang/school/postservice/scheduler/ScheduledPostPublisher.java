package faang.school.postservice.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Component
@Setter
@RequiredArgsConstructor
@Slf4j
public class ScheduledPostPublisher {

    @Value("${post.publisher.scheduler.batch_size}")
    private int batchSize;

    private final PostRepository postRepository;
    private final ThreadPoolExecutor threadPoolExecutor;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void publishScheduledPosts() {
        log.info("Scheduled post publishing started");
        List<Post> filteredPosts = postRepository.findReadyToPublish();
        publishPosts(filteredPosts);
        log.info("Scheduled post publishing executed");
    }

    private void publishPosts(List<Post> filteredPosts) {
        if (filteredPosts.size() > batchSize) {
            for (int i = 0; i < filteredPosts.size(); i += batchSize) {
                int startIndex = i;
                int endIndex = Math.min(i + batchSize, filteredPosts.size());
                threadPoolExecutor.execute(() -> processAndSavePosts(filteredPosts.subList(startIndex, endIndex)));
            }
        } else {
            processAndSavePosts(filteredPosts);
        }
    }

    private void processAndSavePosts(List<Post> posts) {
        LocalDateTime time = LocalDateTime.now();

        posts.forEach(post -> {
            post.setPublished(true);
            post.setPublishedAt(time);
        });
        postRepository.saveAll(posts);
    }
}
package faang.school.postservice.scheduler;

import com.google.common.collect.Lists;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationScheduler {

    @Value("${thread-pool.verification-content-pool.num-of-chunk}")
    private int numOfChunk;

    private final PostService postService;

    @Scheduled(cron = "${schedule.verify-posts.cron}")
    public void verifyPosts() {
        List<Post> posts = postService.findNotReviewedPost();
        if (posts.isEmpty()) {
            log.info("Reviewed posts not found");
            return;
        }

        int chunkSize = posts.size() / numOfChunk + posts.size() % numOfChunk;
        Lists.partition(posts, chunkSize).forEach(postService::verifyPostAsync);
    }
}

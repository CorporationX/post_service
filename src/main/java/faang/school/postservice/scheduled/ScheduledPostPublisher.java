package faang.school.postservice.scheduled;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private final PostService postService;
    private final PostRepository postRepository;
    @Value("${post.config.batch-size}")
    private int batchSize;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public void publishScheduledPosts() {
        List<Post> posts = postRepository.findReadyToPublish();
        List<List<Post>> partitions = ListUtils.partition(posts, batchSize);

        partitions.forEach(postService::publishScheduledPosts);
    }
}
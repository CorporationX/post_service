package faang.school.postservice.service.post;

import faang.school.postservice.client.FollowFeignClient;
import faang.school.postservice.dto.post.PostFeedEvent;
import faang.school.postservice.dto.user.follower.FollowersPage;
import faang.school.postservice.kafka.producer.post.PostFeedProducer;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostFanoutPublisher {

    private final FollowFeignClient followClient;
    private final PostFeedProducer postFeedProducer;

    @Value("${feed.followers.page-size:1000}")
    private int pageSize;
    @Value("${feed.kafka.batch-size:200}")
    private int batchSize;

    @Async("postFanoutExecutor")
    public void paginateFollowersAndPublishBatches(Post post) {
        long authorId = post.getAuthorId();
        Instant publishedAt = post.getPublishedAt().atZone(ZoneOffset.UTC).toInstant();

        String cursor = null;
        while (true) {
            FollowersPage page;
            try {
                page = followClient.getFollowerIds(authorId, cursor, pageSize);
            } catch (Exception e) {
                log.error("Followers fetch failed authorId={} cursor={}", authorId, cursor, e);
                return;
            }
            if (page == null || page.ids() == null || page.ids().isEmpty()) {
                return;
            }
            List<Long> followerIds = page.ids();
            for (int from = 0; from < followerIds.size(); from += batchSize) {
                int to = Math.min(from + batchSize, followerIds.size());
                List<Long> batch = new ArrayList<>(followerIds.subList(from, to));
                postFeedProducer.publishPostCreated(
                        new PostFeedEvent(post.getId(), authorId, batch, publishedAt)
                );
            }
            cursor = page.nextCursor();
            if (cursor == null) {
                return;
            }
        }
    }
}

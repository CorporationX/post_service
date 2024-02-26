package faang.school.postservice.publisher;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostEventPublisher {
    private final AsyncPostEventPublisher asyncPostEventPublisher;
    private final UserServiceClient userServiceClient;

    @Value("${feed.post_batch}")
    private int batchSize;

    public void publish(PostEvent originalEvent) {
        long followeeId = (originalEvent.getUserAuthorId() != null) ? originalEvent.getUserAuthorId() : originalEvent.getProjectAuthorId();
        List<Long> followerIds = userServiceClient.getFollowerIds(followeeId);
        List<List<Long>> followerIdBatches = ListUtils.partition(followerIds, batchSize);

        followerIdBatches.forEach(batch -> {
            PostEvent batchEvent = new PostEvent(originalEvent);
            batchEvent.setFollowerIds(batch);
            asyncPostEventPublisher.asyncPublishBatchEvent(batchEvent);
        });
    }
}

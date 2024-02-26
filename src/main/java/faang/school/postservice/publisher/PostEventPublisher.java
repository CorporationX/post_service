package faang.school.postservice.publisher;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.mapper.PostEventMapper;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventPublisher {
    private final AsyncEventPublisher asyncEventPublisher;
    private final PostEventMapper mapper;
    private final UserServiceClient userServiceClient;

    @Value("${spring.kafka.topics.post.batch}")
    private int batchSize;

    public void publish(Post post) {
        PostEvent originalEvent = mapper.toPostEvent(post);
        long followeeId = (originalEvent.getUserAuthorId() != null) ? originalEvent.getUserAuthorId() : originalEvent.getProjectAuthorId();
        List<Long> followerIds = userServiceClient.getFollowerIds(followeeId);
        List<List<Long>> followerIdBatches = ListUtils.partition(followerIds, batchSize);

        followerIdBatches.forEach(batch -> {
            PostEvent batchEvent = new PostEvent(originalEvent);
            batchEvent.setFollowerIds(batch);
            asyncEventPublisher.asyncPublishBatchEvent(batchEvent);
        });

    }
}

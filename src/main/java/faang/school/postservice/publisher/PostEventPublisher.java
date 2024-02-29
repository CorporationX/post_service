package faang.school.postservice.publisher;

import faang.school.postservice.dto.event_broker.LikePostEvent;
import faang.school.postservice.dto.event_broker.PostEvent;
import faang.school.postservice.service.PostEventPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostEventPublisher extends AsyncEventPublisher<PostEvent>{
    private final PostEventPublisherService publisherService;

    @Value("${spring.kafka.topics.post.name}")
    private String postTopic;
    @Value("${spring.kafka.topics.heat_feed.post}")
    private String heatPostTopic;

    @Override
    protected String getTopicName() {
        return postTopic;
    }

    @Override
    protected String getHeatTopicName() {
        return heatPostTopic;
    }

    public void publish(PostEvent originalEvent) {
        List<List<Long>> followerIdBatches = publisherService.getFollowerIdBatches(originalEvent);

        followerIdBatches.forEach(batch -> {
            PostEvent batchEvent = new PostEvent(originalEvent);
            batchEvent.setFollowerIds(batch);
            asyncPublish(batchEvent);
        });
    }

    public void heatPublish(PostEvent originalEvent) {
        List<List<Long>> followerIdBatches = publisherService.getFollowerIdBatches(originalEvent);

        followerIdBatches.forEach(batch -> {
            PostEvent batchEvent = new PostEvent(originalEvent);
            batchEvent.setFollowerIds(batch);
            asyncHeatPublish(batchEvent);
        });
    }
}

package faang.school.postservice.publisher;

import faang.school.postservice.dto.event_broker.PostEvent;
import faang.school.postservice.service.PostEventPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HeatFeedEventPublisher extends AsyncEventPublisher<PostEvent>{
    private final PostEventPublisherService publisherService;

    @Value("${spring.kafka.topics.heat_feed.name}")
    private String heatFeedTopic;

    @Override
    protected String getTopicName() {
        return heatFeedTopic;
    }

    public void publish(PostEvent originalEvent) {
        List<List<Long>> followerIdBatches = publisherService.getFollowerIdBatches(originalEvent);

        followerIdBatches.forEach(batch -> {
            PostEvent batchEvent = new PostEvent(originalEvent);
            batchEvent.setFollowerIds(batch);
            asyncPublish(batchEvent);
        });
    }
}

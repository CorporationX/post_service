package faang.school.postservice.publisher;

import faang.school.postservice.dto.event_broker.LikePostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikePostEventPublisher extends AsyncEventPublisher<LikePostEvent>{
    @Value("${spring.kafka.topics.like_post.name}")
    private String likePostTopic;
    @Value("${spring.kafka.topics.heat_feed.like_post}")
    private String heatLikeTopic;

    @Override
    protected String getTopicName() {
        return likePostTopic;
    }

    @Override
    protected String getHeatTopicName() {
        return heatLikeTopic;
    }

    public void publish(LikePostEvent event) {
        asyncPublish(event);
    }

    public void heatPublish(LikePostEvent event) {
        asyncHeatPublish(event);
    }
}

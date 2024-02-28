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

    @Override
    protected String getTopicName() {
        return likePostTopic;
    }

    public void publish(LikePostEvent event) {
        asyncPublish(event);
    }


}

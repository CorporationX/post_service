package faang.school.postservice.listener.like;

import faang.school.postservice.dto.notification.PostLikedEvent;
import faang.school.postservice.listener.AbstractEventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PostLikedEventListener extends AbstractEventListener<PostLikedEvent> {

    @KafkaListener(topics = "${spring.kafka.topics.like.post-liked-topic.name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(PostLikedEvent event) {
        event.handle();
    }

    @Override
    public boolean isEventValid(PostLikedEvent event) {
        return false;
    }
}

package faang.school.postservice.listener;

import faang.school.postservice.dto.feed.PostFollowersEvent;
import faang.school.postservice.repository.FeedRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventListener {

    private final AbstractEventListener abstractEventListener;
    private final FeedRedisRepository feedRedisRepository;

    @KafkaListener(
            topics = "${spring.data.kafka.topic.posts.name}",
            groupId = "${spring.data.kafka.consumer.group-id}"
    )
    public void receive(String message, Acknowledgment ack) {
        abstractEventListener.receiveAndHandle(message, PostFollowersEvent.class,
                feedRedisRepository::addPostsForFollowersToFeed, ack);
    }
}

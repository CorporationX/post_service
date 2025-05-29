package faang.school.postservice.listener;

import faang.school.postservice.repository.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeAddedEventListener {

    private final AbstractEventListener abstractEventListener;
    private final PostRedisRepository postRedisRepository;

    @KafkaListener(
            topics = "${spring.data.kafka.topic.like-added.name}",
            groupId = "${spring.data.kafka.consumer.group-id}"
    )
    public void receive(String message, Acknowledgment ack) {
        abstractEventListener.receiveAndHandle(message, Long.class, postRedisRepository::incrementLikes, ack);
    }
}

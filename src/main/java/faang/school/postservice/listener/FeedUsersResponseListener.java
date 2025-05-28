package faang.school.postservice.listener;

import faang.school.postservice.component.FeedHeater;
import faang.school.postservice.dto.feed.UserSubscriptionsEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeedUsersResponseListener {

    private final AbstractEventListener eventListener;
    private final FeedHeater feedHeater;

    @KafkaListener(
            topics = "${spring.data.kafka.topic.feed-users-response.name}",
            groupId = "${spring.data.kafka.consumer.group-id}"
    )
    public void receive(String message, Acknowledgment ack) {
        eventListener.receiveAndHandle(message, UserSubscriptionsEvent.class, feedHeater::processUsersFeed, ack);
    }
}

package faang.school.postservice.publisher.user_ban;

import faang.school.postservice.event.Event;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserBanEventPublisher extends AbstractEventPublisher {

    @Value(value = "${spring.kafka.topics.user-ban-topic.name}")
    private String userBanTopicName;

    @Override
    public void publishEvent(Event event) {
        send(userBanTopicName, event);
    }
}

package faang.school.postservice.publisher.comment;

import faang.school.postservice.event.Event;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractEventPublisher {

    @Value("${spring.kafka.topics.comment-topic.name}")
    private String topicName;

    @Override
    public void publishEvent(Event event) {
        send(topicName, event);
    }
}

package faang.school.postservice.publisher.post;

import faang.school.postservice.mapper.PostViewEventMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.post.PostEventType;
import faang.school.postservice.model.event.post.PostViewedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class PostEventPublisher {
    private final KafkaTemplate<String, PostViewedEvent> kafkaTemplate;
    private final PostViewEventMapper eventMapper;
    private final Clock clock;

    @Value("${spring.kafka.topics.post-viewed.name}")
    private String topic;


    public void publishEvents(Post post, Long viewerId, PostEventType[] eventTypes) {
        LocalDateTime viewedAt = LocalDateTime.now(clock);
        Arrays.stream(eventTypes)
                .forEach(type -> {
                    PostViewedEvent event = eventMapper.toEvent(post, viewerId, viewedAt, type);
                    kafkaTemplate.send(topic, event);
                });
    }
}

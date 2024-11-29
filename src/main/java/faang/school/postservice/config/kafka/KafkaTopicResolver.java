package faang.school.postservice.config.kafka;

import faang.school.postservice.publisher.kafka.events.FeedHeatEvent;
import faang.school.postservice.publisher.kafka.events.PostCommentEvent;
import faang.school.postservice.publisher.kafka.events.PostEvent;
import faang.school.postservice.publisher.kafka.events.PostLikeEvent;
import faang.school.postservice.publisher.kafka.events.PostViewEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KafkaTopicResolver {
    private final Map<Class<?>, String> eventTopicMap;

    public KafkaTopicResolver(KafkaTopicsConfig topicsConfig) {
        eventTopicMap = Map.of(
                PostCommentEvent.class, topicsConfig.getPostCommentsTopic(),
                PostEvent.class, topicsConfig.getPostsTopic(),
                PostLikeEvent.class, topicsConfig.getPostLikesTopic(),
                PostViewEvent.class, topicsConfig.getPostViewsTopic(),
                FeedHeatEvent.class, topicsConfig.getHeatFeedTopic()
        );
    }

    public String resolveTopic(Object event) {
        String topic = eventTopicMap.get(event.getClass());
        if (topic == null) {
            throw new IllegalArgumentException("No topic mapped for event class: " + event.getClass().getName());
        }
        return topic;
    }
}

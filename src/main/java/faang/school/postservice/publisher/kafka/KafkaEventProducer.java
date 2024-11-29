package faang.school.postservice.publisher.kafka;

import faang.school.postservice.config.kafka.KafkaTopicResolver;
import faang.school.postservice.publisher.kafka.events.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicResolver topicResolver;

    public void sendEvent(Object event) {
        String topic = topicResolver.resolveTopic(event);
        kafkaTemplate.send(topic, event);
        log.info("Event of type {} sent to topic {}: {}", event.getClass().getSimpleName(), topic, event);
    }

    public void sendPostEventWithSubgroups(PostEvent event, int subgroupSize) {
        List<Long> followerIds = event.getFollowersIds();
        List<List<Long>> subgroups = splitIntoSubgroups(followerIds, subgroupSize);

        for (List<Long> subgroup : subgroups) {
            PostEvent subgroupEvent = new PostEvent(event.getPostId(), subgroup, event.getPublishedAt());
            sendEvent(subgroupEvent);
            log.info("Subgroup event sent with {} followers for post ID {}", subgroup.size(), event.getPostId());
        }
    }

    private List<List<Long>> splitIntoSubgroups(List<Long> followers, int subgroupSize) {
        List<List<Long>> result = new ArrayList<>();
        for (int i = 0; i < followers.size(); i += subgroupSize) {
            result.add(followers.subList(i, Math.min(i + subgroupSize, followers.size())));
        }
        return result;
    }
}
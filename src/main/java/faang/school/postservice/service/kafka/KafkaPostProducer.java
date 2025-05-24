package faang.school.postservice.service.kafka;

import faang.school.postservice.event.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPostProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final SubscriptionRepository subscriptionRepository;

    public void sendPostCreatedEvent(Long postId, Long authorId) {
        List<Long> subscriberIds = subscriptionRepository.findSubscriberIdsByAuthorId(authorId);

        PostCreatedEvent event = new PostCreatedEvent(postId, subscriberIds);
        kafkaTemplate.send("posts", "postCreatedEvent", event);
        log.info("Sent PostCreatedEvent for postId: {} with {} subscribers", postId, subscriberIds.size());
    }
}
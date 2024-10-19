package faang.school.postservice.cosumer.kafka;

import faang.school.postservice.event.post.PostKafkaEvent;
import faang.school.postservice.repository.cache.NewsFeedCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final NewsFeedCacheRepository newsFeedCacheRepository;

    @KafkaListener(topics = "${spring.kafka.producer.topics.posts}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenEvent(PostKafkaEvent event, Acknowledgment acknowledgment) {
        event.getFollowerIds().forEach(followerId ->
                newsFeedCacheRepository.savePostToFeed(followerId, event.getPostId(), event.getCreatedAt()));
        acknowledgment.acknowledge();
    }

}

package faang.school.postservice.messaging.consumer.kafka;

import faang.school.postservice.config.redis.entity.FeedRedis;
import faang.school.postservice.messaging.dto.PostPublishEvent;
import faang.school.postservice.repository.FeedRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostPublishEventConsumer {

    private final FeedRedisRepository feedRepository;
    @Value("${spring.data.redis.feed-ids-max}")
    private int feedRedisPostIdsMax;

    @KafkaListener(topics = "${kafka.topics.publish-post}", groupId = "${kafka.group.publish-event}")
    public void listenPostEvent(PostPublishEvent event, Acknowledgment acknowledgment) {
        for (Long followerId : event.getFollowersId()) {
            FeedRedis currentFeed = feedRepository.getById(followerId);
            if (currentFeed != null) {
                currentFeed.getPostIds().add(event.getPostId());
                deleteLastPost(currentFeed);
            } else {
                FeedRedis newFeed = new FeedRedis(followerId, new LinkedHashSet<>(Set.of(event.getPostId())));
                feedRepository.save(newFeed);
            }
        }
        acknowledgment.acknowledge();
    }

    private void deleteLastPost(FeedRedis currentFeed) {
        if (currentFeed.getPostIds().size() > feedRedisPostIdsMax) {
            Iterator<Long> iterator = currentFeed.getPostIds().iterator();
            iterator.next();
            iterator.remove();
        }
    }
}

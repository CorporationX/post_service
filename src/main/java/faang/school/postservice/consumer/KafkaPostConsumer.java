package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostKafkaEvent;
import faang.school.postservice.model.redis.FeedRedis;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final PostRepository postRepository;
    private final RedisFeedRepository redisFeedRepository;

    @Value("${spring.data.redis.news-feed-post-ids-max}")
    private int feedRedisPostIdsMax;

    @KafkaListener(topics = "${spring.data.kafka.topics.posts.name}", groupId = "${spring.data.kafka.consumer.group-id}")
    public void listenPostEvent(PostKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Post event received. Author ID: {}", event.getAuthorId());
        List<Long> authorPostIds = postRepository.findPostIdsByAuthorIdOrderByIdDesc(event.getAuthorId());
        for (Long subscriberId : event.getSubscribers()) {
            FeedRedis foundNewsFeed = redisFeedRepository.getById(subscriberId);
            if (foundNewsFeed != null) {
                log.info("Add new post ids for User ID: {} news feed", subscriberId);
                foundNewsFeed.setPostIds(postIdsUpdate(foundNewsFeed.getPostIds(), authorPostIds));
            } else {
                log.info("For user ID: {} create new Feed in Redis", subscriberId);
                TreeSet<Long> ids = new TreeSet<>(Comparator.reverseOrder());
                ids.addAll(authorPostIds.subList(0, authorPostIds.size() > feedRedisPostIdsMax ? feedRedisPostIdsMax : authorPostIds.size()));
                foundNewsFeed = FeedRedis.builder()
                        .id(subscriberId)
                        .postIds(ids)
                        .build();
            }
            redisFeedRepository.save(foundNewsFeed);
        }
        acknowledgment.acknowledge();
    }

    private TreeSet<Long> postIdsUpdate(TreeSet<Long> currentFeedIds, List<Long> authorPostIds) {
        currentFeedIds.addAll(authorPostIds);
        while (currentFeedIds.size() > feedRedisPostIdsMax) {
            currentFeedIds.remove(currentFeedIds.last());
        }
        return currentFeedIds;
    }
}

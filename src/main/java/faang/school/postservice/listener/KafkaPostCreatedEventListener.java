package faang.school.postservice.listener;

import faang.school.postservice.event.PostCreatedEvent;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.repository.redis.feed.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.TreeSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaPostCreatedEventListener extends AbstractKafkaListener<PostCreatedEvent> {

    @Value("${spring.data.redis.news-feed-post-ids-max}")
    private int feedRedisPostIdsMax;
    private final RedisFeedRepository redisFeedRepository;

    @KafkaListener(topics = "${spring.data.kafka.posts-topic}", groupId = "posts")
    public void listen(String data) {
        consume(data, PostCreatedEvent.class, this::handle);
    }

    @Override
    public void handle(PostCreatedEvent event) {
        log.info("Received post created event: {}", event);
        for (Long subscriberId : event.getAuthorFollowerIds()) {
            RedisFeed foundNewsFeed = redisFeedRepository.getById(subscriberId);
            if (foundNewsFeed != null) {
                postIdsUpdate(foundNewsFeed.getPostIds(), event.getPostId());
            } else {
                TreeSet<Long> ids = new TreeSet<>();
                ids.add(event.getPostId());
                foundNewsFeed = RedisFeed.builder()
                        .id(subscriberId)
                        .postIds(ids)
                        .build();
            }
            redisFeedRepository.save(foundNewsFeed);
        }
    }

    private void postIdsUpdate(TreeSet<Long> currentFeedIds, Long authorPostIds) {
        currentFeedIds.add(authorPostIds);
        while (currentFeedIds.size() > feedRedisPostIdsMax) {
            currentFeedIds.remove(currentFeedIds.last());
        }
    }
}

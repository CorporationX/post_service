package faang.school.postservice.consumer;


import faang.school.postservice.model.event.FeedEvent;
import faang.school.postservice.model.event.PostEvent;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final RedisFeedRepository redisFeedRepository;

    @Value("${feed.max-post-size}")
    private int maxPostSizeInFeed;

    @KafkaListener(topics = "${spring.data.kafka.topics.post_topic}", groupId = "${spring.data.kafka.group-id}")
    public void listen(PostEvent postEvent, Acknowledgment acknowledgment) {
        log.debug("Post event received. Author id {}", postEvent.getAuthorId());
        postEvent.getFollowersId().forEach(followerId -> {
            TreeSet<Long> postIds = new TreeSet<>(Comparator.reverseOrder());
            Optional<FeedEvent> feed = redisFeedRepository.findById(followerId);
            if (feed.isPresent()) {
                addNewPostIdInFeed(feed.get(), postIds, postEvent, followerId);
            } else {
                FeedEvent newFeed = new FeedEvent();
                newFeed.setId(followerId);
                newFeed.setPostsId(postIds);
                addNewPostIdInFeed(newFeed, postIds, postEvent, followerId);
            }
        });
        acknowledgment.acknowledge();
    }

    private void addNewPostIdInFeed(FeedEvent feed, TreeSet<Long> postIds, PostEvent postEvent, Long userId) {
        postIds.add(postEvent.getId());
        log.debug("Post {} successfully added in feed {} on user {}", postEvent.getId(), feed.getId(), userId);
        if (postIds.size() >= maxPostSizeInFeed) {
            postIds.remove(postIds.last());
        }
        redisFeedRepository.save(feed);
    }
}

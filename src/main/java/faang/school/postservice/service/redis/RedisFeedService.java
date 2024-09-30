package faang.school.postservice.service.redis;

import faang.school.postservice.entity.redis.Feed;
import faang.school.postservice.event.post.FollowersPostEvent;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisFeedService {
    private final RedisFeedRepository redisFeedRepository;
    @Value("${spring.data.redis.cache.ttl.feed}")
    private long ttlFeed;

    public void addIdPostsInUserId(FollowersPostEvent followersPostEvent) {
        // учесть, что если фида нет, то его нужно создать
        for (Long followersId : followersPostEvent.getFollowersIds()) {
            Feed feed = redisFeedRepository.findById(followersId).orElse(null);
            if (feed == null) {
                redisFeedRepository.save(Feed.builder()
                        .userId(followersId)
                        .posts(new LinkedHashSet<>(Collections.singleton(followersPostEvent.getPostId())))
                        .ttl(ttlFeed)
                        .build());
                log.info("FEED SAVED ----> for userId: {}", followersId);
            } else {
                if (feed.getPosts().size() == 500) {
                    Iterator<Long> iterator = feed.getPosts().iterator();
                    if (iterator.hasNext()) {
                        iterator.next();
                        iterator.remove();
                    }
                }
                feed.getPosts().add(followersPostEvent.getPostId());
                Feed savedFeed = redisFeedRepository.save(feed);
                log.info("Saved feed: {}", savedFeed);
                log.info("FEED UPDATED ----> for userId: {}", followersId);
            }
        }
    }
}

package faang.school.postservice.service;

import faang.school.postservice.dto.event.kafka.NewPostEvent;
import faang.school.postservice.dto.user.CacheUser;
import faang.school.postservice.repository.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final RedisUserRepository redisUserRepository;

    @Value("${spring.feed.max-size}")
    private int maxFeedSize;

    public void addPostToFollowers(NewPostEvent newPostEvent) {
        List<CacheUser> users = StreamSupport.stream(
                        redisUserRepository.findAllById(newPostEvent.getSubscribersIds()).spliterator(), false)
                .toList();

        users.forEach(user -> {
            LinkedHashSet<Long> postIds = user.getPostIdsForFeed();
            postIds.add(newPostEvent.getId());

            if (postIds.size() > maxFeedSize) {
                Iterator<Long> it = postIds.iterator();
                if (it.hasNext()) {
                    it.next();
                    it.remove();
                }
            }
        });
        redisUserRepository.saveAll(users);
    }
}

package faang.school.postservice.cache.feed;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.repository.feed.FeedCacheRepository;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedCacher {

    @Value("${cache.memory.max-feed-size:500}")
    private int feedSize;
    private final Long cacheTtl;
    private final FeedCacheRepository feedCacheRepository;
    private final FeedService feedService;

    @SneakyThrows
    @Async("cacheExecutor")
    @Transactional
    public void cache(List<FeedDto> feedDtos) {
        List<FeedDto> feeds = new ArrayList<>();
        feedDtos.forEach(feedDto -> {
            FeedDto existingFeed = feedService.findById(feedDto.getUserId());
            Set<Long> updatedPosts = new LinkedHashSet<>(feedDto.getPostIds());
            updatedPosts.addAll(existingFeed.getPostIds());

            if (updatedPosts.size() > feedSize) {
                updatedPosts = updatedPosts.stream()
                        .limit(feedSize)
                        .collect(Collectors.toSet());
            }

            existingFeed.setPostIds(updatedPosts);
            existingFeed.setTtl(cacheTtl);
            feeds.add(existingFeed);
        });
        feedCacheRepository.saveAll(feeds);
    }
}

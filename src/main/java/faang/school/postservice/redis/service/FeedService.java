package faang.school.postservice.redis.service;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.mapper.FeedMapper;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.model.AuthorCache;
import faang.school.postservice.redis.model.Feed;
import faang.school.postservice.redis.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final AuthorCacheService authorCacheService;
    private final FeedRepository feedRepository;
    private final FeedMapper feedMapper;
    private final PostCacheMapper postCacheMapper;

    public FeedDto getFeed(Long followerId) {
        Feed feed = feedRepository.findById(followerId).orElse(null);
        if (feed == null) {
            return null;
        }
        return feedMapper.toDto(feed);
    }

    public void fillingFeed(PostDto postDto) {
        AuthorCache authorCache = authorCacheService.getSubscribers(postDto.getAuthorId());
        if (authorCache == null) {
            throw new NullPointerException("authorCache is null");
        }
        authorCache.getSubscribers()
                .forEach(s -> {
                    Feed feed = feedRepository.findById(s)
                            .orElseGet(() -> newFeed(s));
                    feed.getPosts().add(postCacheMapper.toPostCache(postDto));
                    feedRepository.save(feed);
                });
    }

    private Feed newFeed(Long followerId) {
        Feed feed = new Feed(followerId, new ArrayList<>());
        return feedRepository.save(feed);
    }
}

package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.cache.entity.FeedRedisCache;
import faang.school.postservice.redis.cache.entity.PostRedisCache;
import faang.school.postservice.redis.cache.service.feed.FeedRedisCacheService;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRedisCacheService feedRedisCacheService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getNewsFeed(long userId, Pageable pageable) {

        List<PostDto> cachePosts = getFromRedisCache(userId, pageable);

        if (cachePosts != null) {
            return cachePosts;
        }

        return getFromDataBase(userId, pageable);
    }

    private List<PostDto> getFromRedisCache(long userId, Pageable pageable) {

        FeedRedisCache feedRedis = feedRedisCacheService.findByUserId(userId);

        if (feedRedis == null || feedRedis.getPosts().isEmpty()) {
            return null;
        }

        NavigableSet<PostRedisCache> posts = feedRedis.getPosts();

        if (pageable.getOffset() + pageable.getPageSize() > posts.size()) {
            return null;
        }

        List<PostRedisCache> caches = getPage(posts, pageable);
        NavigableSet<PostRedisCache> out = new TreeSet<>();

        for (PostRedisCache cache : caches) {

            if (cache.getAuthor() == null) {
                Post post = getPostFromDataBase(cache.getId());
                out.add(postMapper.toRedisCache(post));
            }

            out.add(cache);
        }

        return out.stream()
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> getFromDataBase(long userId, Pageable pageable) {

        return postRepository.findFeedByUserId(userId, pageable)
                .stream()
                .map(postMapper::toDto)
                .toList();
    }

    private Post getPostFromDataBase(long postId) {

        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found: " + postId));
    }

    private List<PostRedisCache> getPage(NavigableSet<PostRedisCache> posts, Pageable pageable) {

        return posts.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
    }
}

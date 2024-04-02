package faang.school.postservice.service.newsfeed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class NewsFeedService {

    private final RedisPostRepository redisPostRepository;
    private final ZSetOperations<Long, Object> feeds;
    private final UserContext userContext;
    private final PostRepository postRepository;
    private final RedisPostMapper redisPostMapper;

    public List<RedisPost> getFeed(Optional<Long> optionalPostId) {
        Stream<Long> postIdStream;
        if (optionalPostId.isEmpty()) {
            postIdStream = getPostId(0, 20);
        } else {
            postIdStream = getPostId(optionalPostId.get(), 20);
        }
        return getPostsById(postIdStream);
    }

    public Stream<Long> getPostId(long start, long limit) {
        long userId = userContext.getUserId();
        return feeds.range(userId, start, start + limit - 1).stream()
                .map(postId -> (Long) postId).filter(Objects::nonNull);
    }

    public List<RedisPost> getPostsById(Stream<Long> postIdStream) {
        return postIdStream.map(postId -> {
                    var optionalRedisPost = redisPostRepository.findById(postId);
                    if (optionalRedisPost.isEmpty()) {
                        Post post = postRepository
                                .findById(postId)
                                .orElseThrow(() -> new EntityNotFoundException("Post don't found"));
                        return redisPostMapper.toRedisEntity(post);
                    } else {
                        return optionalRedisPost.get();
                    }
                })
                .toList();
    }
}
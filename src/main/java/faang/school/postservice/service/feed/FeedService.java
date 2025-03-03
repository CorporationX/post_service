package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.model.feed.PostCache;
import faang.school.postservice.model.feed.UserCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.feed.RedisFeedRepository;
import faang.school.postservice.repository.feed.RedisPostRepository;
import faang.school.postservice.repository.feed.RedisUserRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final RedisTemplate<String, Long> redisTemplate;
    private final RedisFeedRepository redisFeedRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;

    public Set<FeedPostDto> get(Long userId, Long lastPostId) {
        if (lastPostId == null) {
            lastPostId = 0L;
            return getUserFeed(userId, lastPostId);
        }
        return getUserFeed(userId, lastPostId);
    }

    public Set<Long> getTopPostIdAfterPosition(Long userId, Long position) {
        String key = String.format("feed:%d", userId);
        return Optional.ofNullable(redisTemplate.opsForZSet().reverseRange(key, position, position + 19))
                .map(LinkedHashSet::new)
                .orElseGet(LinkedHashSet::new);
    }

    public Set<FeedPostDto> getUserFeed(Long userId, Long lastPostId) {
        Set<FeedPostDto> feed = new LinkedHashSet<>();
        Set<Long> postIds = getTopPostIdAfterPosition(userId, lastPostId);
        populateFeedPosts(feed, postIds);
        // сохранить фид редис
        return feed;
    }

    private void populateFeedPosts(Set<FeedPostDto> feed, Set<Long> postIds) {
        postIds.forEach(postId -> {
            FeedPostDto feedPost = getPostFromCache(postId);

            Optional<UserCache> userCacheOptional = redisUserRepository.findById(String.valueOf(feedPost.getAuthorId()));
            if (userCacheOptional.isPresent()) {
                UserCache userCache = userCacheOptional.get();
                feedPost.setAuthorUsername(userCache.getUsername());
            } else {
                var user = userServiceClient.getUser(feedPost.getAuthorId());
                //сохранить юзера в кэше, чтобы он был в редисе
                feedPost.setAuthorUsername(user.getUsername());
            }
            feed.add(feedPost);
        });
    }

    private FeedPostDto getPostFromCache(Long postId) {

        Optional<PostCache> postCacheOptional = redisPostRepository.findById(postId.toString());
        if (postCacheOptional.isPresent()) {
            PostCache postCache = postCacheOptional.get();
            return FeedPostDto.builder()
                    .authorId(postCache.getAuthorId())
                    .content(postCache.getContent())
                    .likes(postCache.getLikes())
                    .comments(postCache.getComments())
                    .build();
        } else {
            var post = postService.findPostById(postId);
            // сохранить пост в редис, чтобы он был в кэше
            FeedPostDto feedPost = FeedPostDto.builder()
                    .authorId(post.getAuthorId())
                    .content(post.getContent())
                    .likes((long) post.getLikes().size())
                    .comments((long) post.getComments().size())
                    .build();

            return feedPost;
        }
    }
}

package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.entity.CachedPost;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final UserContext context;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostMapper mapper;

    @Value("${spring.data.redis.feed.limit}")
    private final int limit;

    private static final String USER_FEED_KEY = "user:feed:%s";
    private static final String POSTS_HASH_KEY = "posts:";

    public List<CachedPost> getFeed(int offset) {
        long userId = context.getUserId();
        String userKey = String.format(USER_FEED_KEY, userId);

        Set<String> postsId = stringRedisTemplate.opsForZSet().range(userKey, offset, limit - 1);
        if (postsId == null) {
            log.info("Для юзера {} не нашлось постов в кеше, иду в базу", userId);
            fillFeedFromDataBase();
        } //допилить поход в базу за его постами

        return postsId.stream()
                .map(id -> (CachedPost) redisTemplate
                        .opsForHash()
                        .get(POSTS_HASH_KEY, id))
                .filter(Objects::nonNull)
                .toList();
    }

    private List<CachedPost> fillFeedFromDataBase() {
        List<Long> followees = userRepository.findFolloweesIdsByUserId(context.getUserId());

        List<Post> preparedPosts =  followees
                .stream()
                .map(postRepository::findByAuthorId)
                .flatMap(List::stream)
                .toList(); // Замапить

        return null;
    }



}

package faang.school.postservice.cache;

import faang.school.postservice.cache.repository.AuthorCacheRepository;
import faang.school.postservice.cache.repository.FeedCacheRepository;
import faang.school.postservice.cache.repository.PostCacheRepository;
import faang.school.postservice.dto.user.GetUsersDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.author.AuthorMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FeedHeater {

    @Value("${cache.feed.collection}")
    private String feedCollection;

    @Value("${cache.posts.collection}")
    private String postCollection;

    @Value("${cache.users.collection}")
    private String usersCollection;

    @Value("${feed-heater.size:500}")
    private Integer postsCacheSize;

    private final PostRepository postRepository;
    private final AuthorCacheRepository authorCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final FeedCacheRepository feedCacheRepository;
    private final UserService userService;
    private final AuthorMapper authorMapper;
    private final PostMapper postMapper;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void heat(List<Long> followerIds) {
        String lockKey = "heat:lock";
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", 10, TimeUnit.MINUTES);

        if (!Boolean.TRUE.equals(locked)) {
            throw new IllegalStateException("Прогрев уже выполняется");
        }

        try {
            List<Post> posts = followerIds.parallelStream()
                    .map(followerId -> postRepository.findFeedPostsForHeating(
                            followerId,
                            PageRequest.of(0, postsCacheSize)))
                    .flatMap(List::stream)
                    .distinct().toList();

            Set<Long> authorsIds = posts.stream().map(Post::getAuthorId).collect(Collectors.toSet());

            List<UserDto> authorsDto = userService.getUsersByIds(GetUsersDto.builder()
                    .ids(new ArrayList<>(authorsIds))
                    .build());

            postCacheRepository.saveAll(postMapper.toPostCacheList(posts));
            authorCacheRepository.saveAll(authorsDto.stream().map(authorMapper::toAuthorCache).toList());

            followerIds.parallelStream().forEach(followerId -> {
                List<Post> followerPosts = postRepository.findFeedPostsForHeating(
                        followerId,
                        PageRequest.of(0, postsCacheSize));

                feedCacheRepository.saveAll(followerId, followerPosts);
            });
        } finally {
            redisTemplate.delete(lockKey);
        }
    }
}
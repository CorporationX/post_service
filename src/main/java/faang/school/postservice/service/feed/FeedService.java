package faang.school.postservice.service.feed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.User;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String REDIS_FEED_PREFIX = "feed:";
    private static final String REDIS_POSTS = "posts";
    private static final String REDIS_USERS = "users";
    private static final int PAGE_SIZE = 20;

    public List<PostDto> getUserFeed(Long lastPostId) {

        Long userId = getCurrentUserId();

        List<Long> postIds = fetchPostIdsFromRedis(userId, lastPostId);

        return postIds.stream()
                .map(this::getPostDetails)
                .collect(Collectors.toList());
    }

    private List<Long> fetchPostIdsFromRedis(Long userId, Long lastPostId) {
        String redisKey = REDIS_FEED_PREFIX + userId;

        List<Object> redisPostIds = redisTemplate.opsForList().range(redisKey, 0, -1);
        List<Long> postIds = redisPostIds != null
                ? redisPostIds.stream().map(obj -> Long.parseLong(obj.toString())).collect(Collectors.toList())
                : List.of();

        if (postIds.isEmpty()) {
            return postRepository.findTop20ByOrderByCreatedAtDesc()
                    .stream()
                    .map(Post::getId)
                    .collect(Collectors.toList());
        }

        if (lastPostId == null) {
            return postIds.stream().limit(PAGE_SIZE).collect(Collectors.toList());
        }

        int index = postIds.indexOf(lastPostId);
        if (index == -1 || index + 1 >= postIds.size()) {
            return List.of();
        }

        return postIds.subList(index + 1, Math.min(index + 1 + PAGE_SIZE, postIds.size()));
    }

    private PostDto getPostDetails(Long postId) {
        Post post = getPostFromCacheOrDB(postId);
        User user = getUserFromCacheOrDB(post.getAuthorId());

        return PostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .author(new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .build();
    }

    private Post getPostFromCacheOrDB(Long postId) {
        Object postData = redisTemplate.opsForHash().get(REDIS_POSTS, postId);
        Post post = postData != null ? deserialize(postData, Post.class) : null;

        if (post == null) {
            post = postRepository.findById(postId).orElseThrow();
            redisTemplate.opsForHash().put(REDIS_POSTS, postId, serialize(post));
        }

        return post;
    }

    private User getUserFromCacheOrDB(Long userId) {
        Object userData = redisTemplate.opsForHash().get(REDIS_USERS, userId);
        User user = userData != null ? deserialize(userData, User.class) : null;

        if (user == null) {
            user = userRepository.findById(userId).orElseThrow();
            redisTemplate.opsForHash().put(REDIS_USERS, userId, serialize(user));
        }

        return user;
    }

    private <T> T deserialize(Object data, Class<T> clazz) {
        try {
            return objectMapper.readValue(data.toString(), clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize Redis data", e);
        }
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    private Long getCurrentUserId() {

        return 1L;
    }
}



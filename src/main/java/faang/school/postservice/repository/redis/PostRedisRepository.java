package faang.school.postservice.repository.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostCountsProjection;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Класс для сохранения поста в коллекцию {@code posts} в Redis
 *
 * @author Linempy
 * @since 26.09.2025
 */
@Slf4j
@Repository
public class PostRedisRepository {

    @Value("${redis.schema.post.key}")
    private String keyPost;

    @Value("${redis.schema.post.ttl-day}")
    private int ttlDay;

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;
    private final PostMapper mapper;
    private final ObjectMapper objectMapper;

    public PostRedisRepository(@Qualifier("redisDataTemplate") RedisTemplate<String, Object> redisTemplate,
                               PostRepository postRepository,
                               PostMapper mapper,
                               ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }

    public void savePost(PostRedisDto post) {
        String key = getKeyForPost(post.id());
        redisTemplate.opsForValue().set(key, post, Duration.ofDays(ttlDay));
        log.info("Пост id={} был сохранен в Redis", post.id());
    }

    public void savePosts(List<PostRedisDto> posts) {
        try {
            Map<String, PostRedisDto> postMap = new HashMap<>();
            for (PostRedisDto post : posts) {
                String key = getKeyForPost(post.id());
                postMap.put(key, post);
            }

            redisTemplate.opsForValue().multiSet(postMap);

            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (String key : postMap.keySet()) {
                    connection.expire(
                            Objects.requireNonNull(redisTemplate.getStringSerializer().serialize(key)),
                            Duration.ofDays(ttlDay).getSeconds()
                    );
                }
                return null;
            });

            log.info("Сохранено {} постов в Redis с TTL {} дней",
                    posts.size(), ttlDay);
        } catch (Exception e) {
            log.error("Ошибка при сохранении постов в Redis: {}", e.getMessage());
        }
    }

    public PostRedisDto getPost(Long id) {
        String key = getKeyForPost(id);

        return Optional.ofNullable((PostRedisDto) redisTemplate.opsForValue().get(key))
            .orElseGet(() -> {
                    PostRedisDto post = processGetRedisDtoFromDb(id);
                    savePost(post);
                    return post;
                }
            );
    }

    public List<PostRedisDto> getPosts(List<Long> ids) {
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long userId : ids) {
                String key = getKeyForPost(userId);
                connection.get(Objects.requireNonNull(redisTemplate.getStringSerializer().serialize(key)));
            }
            return null;
        });

        return results.stream()
                .filter(Objects::nonNull)
                .map(obj -> objectMapper.convertValue(obj, PostRedisDto.class))
                .toList();
    }

    public PostRedisDto processGetRedisDtoFromDb(Long id) {
        Post postFromDb = postRepository.findPostOrThrow(id);
        PostCountsProjection counts = postRepository.findPostCounts(id);
        return mapper.toRedisDto(postFromDb, counts);
    }

    private String getKeyForPost(Long id) {
        return String.format(keyPost, id);
    }
}
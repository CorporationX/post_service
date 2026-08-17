package faang.school.postservice.repository.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostStatisticProjection;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Класс для сохранения поста в коллекцию {@code posts} в Redis
 *
 * @author Linempy
 * @since 26.09.2025
 */
@Slf4j
@Repository
public class PostRedisRepository {

    private static final String POST_TEMPLATE = "post:%d";

    @Value("${redis.schema.post.ttl-day}")
    private int ttlDay;

    @Value("${redis.schema.comment.max-size}")
    private int maxSizeComments;

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
        String key = getFormattedKey(post.id());
        redisTemplate.opsForValue().set(key, post, Duration.ofDays(ttlDay));
        log.debug("Пост id={} был сохранен в Redis", post.id());
    }

    public void savePosts(List<PostRedisDto> posts) {
        try {
            Map<String, PostRedisDto> postMap = new HashMap<>();
            for (PostRedisDto post : posts) {
                String key = getFormattedKey(post.id());
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
        String key = getFormattedKey(id);
        Object data = redisTemplate.opsForValue().get(key);

        if (data != null) {
            try {
                PostRedisDto post = objectMapper.convertValue(data, PostRedisDto.class);
                return post;
            } catch (IllegalArgumentException e) {
                log.warn("Ошибка конвертации Redis data в PostRedisDto для ключа: {}", key);
            }
        }

        PostRedisDto post = processGetRedisDtoFromDb(id);
        savePost(post);
        return post;
    }

    public List<PostRedisDto> getPosts(List<Long> ids) {
        List<Object> results = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Long userId : ids) {
                String key = getFormattedKey(userId);
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
        PostStatisticProjection counts = postRepository.findPostCounts(id);
        return mapper.toRedisDto(postFromDb, counts.getLikeCount(), counts.getCommentCount());
    }

    public void updateLatestComments(Long postId, Long commentId) {
        String key = getFormattedKey(postId);
        PostRedisDto post = processGetRedisDtoFromDb(postId);
        if (post != null) {
            List<Long> currentComments = post.latestComments();
            List<Long> updatedComments = currentComments != null
                    ? new ArrayList<>(currentComments) : new ArrayList<>();

            updatedComments.add(0, commentId);
            if (updatedComments.size() > maxSizeComments) {
                updatedComments = updatedComments.subList(0, maxSizeComments);
            }

            PostRedisDto updatedPost = mapper.toUpdateComments(post, updatedComments);
            redisTemplate.opsForValue().set(key, updatedPost);
        }
    }

    private String getFormattedKey(Long id) {
        return String.format(POST_TEMPLATE, id);
    }
}
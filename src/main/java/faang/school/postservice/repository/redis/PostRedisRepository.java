package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.post.PostStatisticProjection;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * Класс для сохранения поста в коллекцию {@code posts} в Redis
 *
 * @author Linempy
 * @since 26.09.2025
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PostRedisRepository {

    private static final String POST_TEMPLATE = "post:%d";

    @Value("${redis.schema.post.ttl-day}")
    private int ttlDay;

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;
    private final PostMapper mapper;

    public void savePost(PostRedisDto post) {
        String key = getFormattedKey(post.id());
        redisTemplate.opsForValue().set(key, post, Duration.ofDays(ttlDay));
        log.debug("Пост id={} был сохранен в Redis", post.id());
    }

    public PostRedisDto getPost(Long id) {
        String key = getFormattedKey(id);

        return Optional.ofNullable((PostRedisDto) redisTemplate.opsForValue().get(key))
            .orElseGet(() -> {
                    PostRedisDto post = processGetRedisDtoFromDb(id);
                    savePost(post);
                    return post;
                }
            );
    }

    public PostRedisDto processGetRedisDtoFromDb(Long id) {
        Post postFromDb = postRepository.findPostOrThrow(id);
        PostStatisticProjection counts = postRepository.findPostCounts(id);
        return mapper.toRedisDto(postFromDb, counts.getLikeCount(), counts.getCommentCount());
    }

    private String getFormattedKey(Long id) {
        return String.format(POST_TEMPLATE, id);
    }


}
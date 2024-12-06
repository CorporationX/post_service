package faang.school.postservice.repository.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.properties.redis.post.RedisPostProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.ZoneOffset;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostCacheRepository {
    private static final String POST_LIST_KEY = "posts:list";

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisPostProperties redisPostProperties;
    private final ObjectMapper objectMapper;

    public void savePostToRedis(PostDto postDto) {
        long score = postDto.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
        String postId = postDto.getId().toString();

        ListOperations<String, Object> listOps = redisTemplate.opsForList();
        listOps.rightPush(POST_LIST_KEY, postDto);

        ZSetOperations<String, Object> zSetOps = redisTemplate.opsForZSet();
        zSetOps.add(redisPostProperties.getSetKey(), postId, score);
    }


    public void saveAuthorToRedis(Long id) {
        String authorId = String.valueOf(id);
        String authorValueKey = "author:" + authorId; // Ключ для значения
        String authorSetKey = "authors:set";          // Ключ для множества

        redisTemplate.opsForValue().set(authorValueKey, authorId,
                redisPostProperties.getAuthor().getLiveTime(), redisPostProperties.getAuthor().getTimeUnit());
        redisTemplate.opsForSet().add(authorSetKey, authorId);
        log.info("Author success saved in cache: {}", authorId);
    }
}

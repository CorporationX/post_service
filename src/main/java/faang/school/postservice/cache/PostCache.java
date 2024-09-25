package faang.school.postservice.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PostCache extends AbstractCache {
    private final HashOperations<String, String, String> hashOperations;
    private final ObjectMapper objectMapper;

    public PostCache(RedisTemplate<String, Object> redisTemplate,
                     HashOperations<String, String, String> hashOperations,
                     ObjectMapper objectMapper) {
        super(redisTemplate);
        this.hashOperations = hashOperations;
        this.objectMapper = objectMapper;
    }

    @Value("${spring.data.redis.properties.ttl}")
    private int ttl;

    @Value("${spring.data.redis.keys.post}")
    private String postsKeyName;

    @Retryable(retryFor = {OptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100, multiplier = 3))
    public void save(PostDto postDto) {
            String postKey = preparePostKey(postDto.getId());

            Boolean success = executeTransactionalOperation(postKey, (connection -> {
                String jsonValue = writeAsString(postDto);
                connection.hashCommands().hSet(postsKeyName.getBytes(), postKey.getBytes(), jsonValue.getBytes());
                connection.keyCommands().expire(postKey.getBytes(), ttl);
                return true;
            }));

            if (success == null || !success) {
                throw new OptimisticLockingFailureException(String.format("Unsuccessfully trying to save post %s to posts cache", postDto.getId()));
            }

            log.info(String.format("Successfully trying to save post %s to posts cache", postDto.getId()));
    }

    @Retryable(retryFor = {OptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100, multiplier = 3))
    public void addLike(PostDto postDto) {
        String postKey = preparePostKey(postDto.getId());

        Boolean success = executeTransactionalOperation(postKey, (connection -> {
            postDto.setLikesAmount(postDto.getLikesAmount() + 1);
            String jsonValue = writeAsString(postDto);
            connection.hashCommands().hSet(postsKeyName.getBytes(), postKey.getBytes(), jsonValue.getBytes());
            connection.keyCommands().expire(postKey.getBytes(), ttl);
            return true;
        }));

        if (success == null || !success) {
            throw new OptimisticLockingFailureException(String.format("Unsuccessfully trying to increment post %s likes in posts cache", postDto.getId()));
        }
    }

    public List<PostDto> getByList(List<Long> postIds) {
        List<String> preparedPostKeys = postIds.stream().map(this::preparePostKey).toList();
        List<String> posts = hashOperations.multiGet(postsKeyName, preparedPostKeys);

        return posts.stream()
                .map(this::readToPostDto)
                .toList();
    }

    private String preparePostKey(Long postId) {
        return String.format("%s_%s", postsKeyName, postId);
    }

    public PostDto readToPostDto(String value) {
        try {
            return objectMapper.readValue(value, PostDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
